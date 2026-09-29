package com.crm.webscrape;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

import org.springframework.boot.system.ApplicationHome;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.io.File;
import java.lang.Class;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebScrape {

    private Page page;
    private Browser browser;
    private Playwright playwright;
    private Map<String, String> runtimeStorage = new HashMap<>();
    private WebReturns webReturns = new WebReturns();
    private List<MailBox> mailboxes = new ArrayList<>();

    public WebScrape(Path jsonPath, Map<String, String> params) {
        System.out.println("[WebScrape] Starting scraper process...");
        System.out.println("[WebScrape] Reading and validating JSON: " + jsonPath);
        List<Action> actionList = parseJSON(jsonPath);

        if (actionList == null) {
            System.err.println("[WebScrape] Something went wrong reading JSON data. Aborting execution.");
            return;
        }

        System.out.println("[WebScrape] JSON parsed correctly. Actions to execute: " + actionList.size());

        /* Copy params into runtimeStorage to keep it mutable */
        if (params != null) {
            this.runtimeStorage = new HashMap<>(params);
            System.out.println("[WebScrape] Initialized runtime storage with " + params.size() + " parameters: " + params.keySet());
        } else {
            System.out.println("[WebScrape] No initial parameters provided.");
        }

        /* Initializing Playwright */
        System.out.println("[WebScrape] Launching Playwright Chromium instance...");
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                .setChannel("chrome")
                .setHeadless(false)
                .setArgs(Arrays.asList("--disable-blink-features=AutomationControlled", "--start-maximized"))
        );

        BrowserContext context = browser.newContext(
            new Browser.NewContextOptions()
                .setViewportSize(null)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
        );

        page = context.newPage();
        System.out.println("[WebScrape] Browser context and page successfully initialized.");

        /* Executing actions */
        Action[] allActions = actionList.toArray(new Action[0]);
        
        for (int i = 0; i < allActions.length; i++) {
            System.out.println("[WebScrape] Executing action " + (i + 1) + " of " + allActions.length + ": " + allActions[i].getClass().getSimpleName());
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            
            try {
                runAction(allActions[i]);
            } catch (Exception e) {
                System.err.println("[WebScrape] Action failed at step " + (i + 1) + ": " + e.getMessage());
                browser.close();
                playwright.close();
                throw e; // rethrow or use break/return depending on how you want to stop execution
            }
        }

        System.out.println("[WebScrape] All actions finished. Closing browser and Playwright session...");
        browser.close();
        playwright.close();
        System.out.println("[WebScrape] Browser session closed successfully.");
    }

    public WebReturns getReturnData() {
        return webReturns;
    }

    private void runAction(Action action) {
        boolean implemented = false;
        Method[] classMethods = this.getClass().getDeclaredMethods();

        for (int i = 0; i < classMethods.length; i++) {
            Parameter[] currentActionParams = classMethods[i].getParameters();

            if (currentActionParams.length == 1 &&
                action.getClass() == currentActionParams[0].getType()) {

                implemented = true;
                System.out.println("[WebScrape] Invoking handler method: " + classMethods[i].getName() + "(" + action.getClass().getSimpleName() + ")");

                try {
                    classMethods[i].setAccessible(true);
                    classMethods[i].invoke(this, action);
                } catch (Exception e) {
                    System.err.println("[WebScrape] Exception thrown during execution of " + action.getClass().getSimpleName());
                    browser.close();
                    playwright.close();           
                    throw new RuntimeException(
                        "Error executing action: " + action.getClass().getSimpleName(),
                        e
                    );
                }

                break;
            }
        }

        if (!implemented) {
            System.err.println("[WebScrape] Error: Unrecognized action class -> " + action.getClass().getName());
            throw new RuntimeException(
                "Action not recognized: " + action.getClass().getName()
            );
        }
    }

    private List<Action> parseJSON(Path jsonPath) {
        try {
            System.out.println("[WebScrape] Parsing JSON configuration file at path: " + jsonPath.toAbsolutePath());
            ObjectMapper mapper = new ObjectMapper();

            // Dynamically register all static inner classes extending Action
            for (Class<?> subClass : Action.class.getDeclaredClasses()) {
                if (Action.class.isAssignableFrom(subClass)) {
                    mapper.registerSubtypes(subClass);
                }
            }

            List<Action> actions = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Action>>() {});
            System.out.println("[WebScrape] Successfully deserialized " + (actions != null ? actions.size() : 0) + " actions.");
            return actions;
        } catch (Exception e) {
            System.err.println("[WebScrape] JSON Parsing failed!");
            e.printStackTrace();
            return null;
        }
    }

    /* Actual Playwright functions with logging */

    private void navigate(Action.Navigate navAction) {
        System.out.println("[Action: Navigate] Navigating to URL: " + navAction.url);
        page.navigate(navAction.url);
        System.out.println("[Action: Navigate] Successfully navigated to: " + navAction.url);
    }

    private void click(Action.Click clickAction) {
        System.out.println("[Action: Click] Target selector: '" + clickAction.selector + "'");
        Locator[] matches = page.locator(clickAction.selector).all().toArray(new Locator[0]);
        System.out.println("[Action: Click] Found " + matches.length + " match(es).");

        if (matches.length == 0) {
            throw new RuntimeException("There doesn't seem to be any matches for selector '" + clickAction.selector + "'");
        }

        if (clickAction.expect != null && matches.length != clickAction.expect) {
            throw new RuntimeException("The # of matches is unexpected for: '" + clickAction.selector + "'. Expected: " + clickAction.expect + ", Found: " + matches.length);
        }

        if (clickAction.nth == null) {
            System.out.println("[Action: Click] Clicking all matched elements...");
            for (int i = 0; i < matches.length; i++) {
                System.out.println("[Action: Click] Clicking index: " + i);
                matches[i].click();
            }
        } else {
            System.out.println("[Action: Click] Clicking specific indices: " + clickAction.nth);
            for (int i = 0; i < clickAction.nth.size(); i++) {
                int targetIndex = clickAction.nth.get(i);
                System.out.println("[Action: Click] Clicking index: " + targetIndex);
                matches[targetIndex].click();
            }
        }
        System.out.println("[Action: Click] Click completed.");
    }

    private void fillNormal(Action.FillNormal fillAction) {
        System.out.println("[Action: FillNormal] Target selector: '" + fillAction.selector + "', Text: '" + fillAction.text + "'");
        Locator[] matches = page.locator(fillAction.selector).all().toArray(new Locator[0]);
        System.out.println("[Action: FillNormal] Found " + matches.length + " match(es).");

        if (matches.length == 0) {
            throw new RuntimeException("There doesn't seem to be any matches for selector '" + fillAction.selector + "'");
        }

        if (fillAction.expect != null && matches.length != fillAction.expect) {
            throw new RuntimeException("The # of matches is unexpected for: '" + fillAction.selector + "'. Expected: " + fillAction.expect + ", Found: " + matches.length);
        }

        if (fillAction.nth == null) {
            System.out.println("[Action: FillNormal] Filling all matched elements...");
            for (int i = 0; i < matches.length; i++) {
                System.out.println("[Action: FillNormal] Filling index: " + i);
                matches[i].fill(fillAction.text);
            }
        } else {
            System.out.println("[Action: FillNormal] Filling specific indices: " + fillAction.nth);
            for (int i = 0; i < fillAction.nth.size(); i++) {
                int targetIndex = fillAction.nth.get(i);
                System.out.println("[Action: FillNormal] Filling index: " + targetIndex);
                matches[targetIndex].fill(fillAction.text);
            }
        }
        System.out.println("[Action: FillNormal] Fill completed.");
    }

    private void fillVar(Action.FillVar fillAction) {
        String varValue = runtimeStorage.get(fillAction.varName);
        System.out.println("[Action: FillVar] Target selector: '" + fillAction.selector + "', Var: '" + fillAction.varName + "' (Resolved value: '" + varValue + "')");
        
        Locator[] matches = page.locator(fillAction.selector).all().toArray(new Locator[0]);
        System.out.println("[Action: FillVar] Found " + matches.length + " match(es).");

        if (matches.length == 0) {
            throw new RuntimeException("There doesn't seem to be any matches for selector '" + fillAction.selector + "'");
        }

        if (fillAction.expect != null && matches.length != fillAction.expect) {
            throw new RuntimeException("The # of matches is unexpected for: '" + fillAction.selector + "'. Expected: " + fillAction.expect + ", Found: " + matches.length);
        }

        if (fillAction.nth == null) {
            System.out.println("[Action: FillVar] Filling all matched elements...");
            for (int i = 0; i < matches.length; i++) {
                System.out.println("[Action: FillVar] Filling index: " + i);
                matches[i].fill(varValue);
            }
        } else {
            System.out.println("[Action: FillVar] Filling specific indices: " + fillAction.nth);
            for (int i = 0; i < fillAction.nth.size(); i++) {
                int targetIndex = fillAction.nth.get(i);
                System.out.println("[Action: FillVar] Filling index: " + targetIndex);
                matches[targetIndex].fill(varValue);
            }
        }
        System.out.println("[Action: FillVar] FillVar completed.");
    }

    private void select(Action.SelectNormal selectAction) {
        System.out.println("[Action: SelectNormal] Target selector: '" + selectAction.selector + "', Target option string: '" + selectAction.optionString + "'");
        Locator[] matches = page.locator(selectAction.selector).all().toArray(new Locator[0]);
        System.out.println("[Action: SelectNormal] Found " + matches.length + " match(es).");

        if (matches.length == 0) {
            throw new RuntimeException("There doesn't seem to be any matches for selector '" + selectAction.selector + "'");
        }

        if (selectAction.expect != null && matches.length != selectAction.expect) {
            throw new RuntimeException("The # of matches is unexpected for: '" + selectAction.selector + "'. Expected: " + selectAction.expect + ", Found: " + matches.length);
        }

        if (selectAction.nth == null) {
            System.out.println("[Action: SelectNormal] Selecting options for all matched elements...");
            for (int i = 0; i < matches.length; i++) {
                Locator option = matches[i].locator("option").filter(
                    new Locator.FilterOptions().setHasText(selectAction.optionString)
                ).first();

                String value = option.getAttribute("value");
                System.out.println("[Action: SelectNormal] Found option value '" + value + "' for index " + i + ". Selecting...");
                matches[i].selectOption(value);
            }
        } else {
            System.out.println("[Action: SelectNormal] Selecting options for specific indices: " + selectAction.nth);
            for (int i = 0; i < selectAction.nth.size(); i++) {
                int targetIndex = selectAction.nth.get(i);
                Locator option = matches[targetIndex].locator("option").filter(
                    new Locator.FilterOptions().setHasText(selectAction.optionString)
                ).first();

                String value = option.getAttribute("value");
                System.out.println("[Action: SelectNormal] Found option value '" + value + "' for index " + targetIndex + ". Selecting...");
                matches[targetIndex].selectOption(value);
            }
        }
        System.out.println("[Action: SelectNormal] Select completed.");
    }

    private void selectVar(Action.SelectVar selectAction) {
        String optionString = runtimeStorage.get(selectAction.optionVarName);
        System.out.println("[Action: SelectVar] Target selector: '" + selectAction.selector + "', Var: '" + selectAction.optionVarName + "' (Resolved option: '" + optionString + "')");
        
        Locator[] matches = page.locator(selectAction.selector).all().toArray(new Locator[0]);
        System.out.println("[Action: SelectVar] Found " + matches.length + " match(es).");

        if (matches.length == 0) {
            throw new RuntimeException("There doesn't seem to be any matches for selector '" + selectAction.selector + "'");
        }

        if (selectAction.expect != null && matches.length != selectAction.expect) {
            throw new RuntimeException("The # of matches is unexpected for: '" + selectAction.selector + "'. Expected: " + selectAction.expect + ", Found: " + matches.length);
        }

        if (selectAction.nth == null) {
            System.out.println("[Action: SelectVar] Selecting options for all matched elements...");
            for (int i = 0; i < matches.length; i++) {
                Locator option = matches[i].locator("option").filter(
                    new Locator.FilterOptions().setHasText(optionString)
                ).first();

                String value = option.getAttribute("value");
                System.out.println("[Action: SelectVar] Found option value '" + value + "' for index " + i + ". Selecting...");
                matches[i].selectOption(value);
            }
        } else {
            System.out.println("[Action: SelectVar] Selecting options for specific indices: " + selectAction.nth);
            for (int i = 0; i < selectAction.nth.size(); i++) {
                int targetIndex = selectAction.nth.get(i);
                Locator option = matches[targetIndex].locator("option").filter(
                    new Locator.FilterOptions().setHasText(optionString)
                ).first();

                String value = option.getAttribute("value");
                System.out.println("[Action: SelectVar] Found option value '" + value + "' for index " + targetIndex + ". Selecting...");
                matches[targetIndex].selectOption(value);
            }
        }
        System.out.println("[Action: SelectVar] SelectVar completed.");
    }

    private void wait(Action.Wait waitAction) {
        System.out.println("[Action: Wait] Waiting for " + waitAction.timeout + " ms...");
        page.waitForTimeout(waitAction.timeout);
        System.out.println("[Action: Wait] Wait completed.");
    } 

    private void waitFor(Action.WaitFor waitForAction) {
        System.out.println("[Action: WaitFor] Waiting for selector: '" + waitForAction.selector + "'" 
            + (waitForAction.atLeast != null ? " with at least " + waitForAction.atLeast + " match(es)" : "")
            + " and timeout: " + (waitForAction.timeout != null ? waitForAction.timeout + " ms" : "0 (infinite)"));

        double timeout = waitForAction.timeout != null ? waitForAction.timeout : 0;

        if (waitForAction.atLeast != null && waitForAction.atLeast > 0) {
            Locator locator = page.locator(waitForAction.selector);

            // Target the Nth required item (0-indexed, so atLeast - 1)
            Locator targetElement = locator.nth(waitForAction.atLeast - 1);
            
            // Wait for the Nth element to attach/appear
            targetElement.waitFor(new Locator.WaitForOptions().setTimeout(timeout));
        } else {
            // Fallback to waiting for a single element
            Locator locator = page.locator(waitForAction.selector).first();
            locator.waitFor(new Locator.WaitForOptions().setTimeout(timeout));
        }

        System.out.println("[Action: WaitFor] Element condition met for selector: '" + waitForAction.selector + "'");
    }

    private void conditional(Action.Conditional condAction) {
        System.out.println("[Action: Conditional] Checking existence of selector: '" + condAction.selector + "'");
        Locator[] matches = page.locator(condAction.selector)
            .all()
            .toArray(new Locator[0]);

        if (matches.length > 0) {
            System.out.println("[Action: Conditional] Matched selector: '" + condAction.selector + "' (" + matches.length + " match(es) found). Executing IF action...");
            runAction(condAction.ifAction);
        } else {
            System.out.println("[Action: Conditional] Did NOT match selector: '" + condAction.selector + "'");
            if (condAction.elseAction != null) {
                System.out.println("[Action: Conditional] Executing ELSE action...");
                runAction(condAction.elseAction);
            } else {
                System.out.println("[Action: Conditional] No ELSE action provided. Skipping.");
            }
        }
    }

    private void storeString(Action.StoreString strStoreAc) {
        System.out.println("[Action: StoreString] Storing string to variable name: '" + strStoreAc.stringName + "' from selector: '" + strStoreAc.selector + "' (Attr: " + strStoreAc.attr + ", Nth: " + strStoreAc.nth + ")");
        
        Locator[] matches = page.locator(strStoreAc.selector).all().toArray(new Locator[0]);
        String finalString;

        if (matches.length == 0) {
            throw new RuntimeException("There doesn't seem to be any matches for '" + strStoreAc.selector + "'");
        }

        if (matches.length > 1 && strStoreAc.nth == null) {
            throw new RuntimeException("There is more than one selector for '" + strStoreAc.selector + "', and an nth is not specified!");
        } 

        if (strStoreAc.attr == null) {
            if (strStoreAc.nth == null) {
                finalString = matches[0].textContent();
            } else {
                finalString = matches[strStoreAc.nth].textContent();
            }
        } else {
            if (strStoreAc.nth == null) {
                finalString = matches[0].getAttribute(strStoreAc.attr);
            } else {
                finalString = matches[strStoreAc.nth].getAttribute(strStoreAc.attr);
            }
        }

        System.out.println("[Action: StoreString] Extracted Value: '" + finalString + "'");
        webReturns.storeString(strStoreAc.stringName, finalString);
    }

    private void storeList(Action.StoreList listStoreAc) {
        System.out.println("[Action: StoreList] Storing list to variable name: '" + listStoreAc.listName + "' from selector: '" + listStoreAc.selector + "'");
        
        Locator[] matches = page.locator(listStoreAc.selector).all().toArray(new Locator[0]);
        System.out.println("[Action: StoreList] Found " + matches.length + " match(es).");

        if (matches.length == 0) {
            throw new RuntimeException("There doesn't seem to be any matches for '" + listStoreAc.selector + "'");
        }

        if (listStoreAc.expect != null && matches.length != listStoreAc.expect) {
            throw new RuntimeException("The # of matches is unexpected for: '" + listStoreAc.selector + "'. Expected: " + listStoreAc.expect + ", Found: " + matches.length);
        }

        List<String> returnList = new ArrayList<>();

        if (listStoreAc.nth == null) {
            for (int i = 0; i < matches.length; i++) {
                if (listStoreAc.attr != null) {
                    returnList.add(matches[i].getAttribute(listStoreAc.attr));
                } else {
                    returnList.add(matches[i].textContent());
                }
            }
        } else {
            for (int i = 0; i < listStoreAc.nth.size(); i++) {
                int index = listStoreAc.nth.get(i);
                if (listStoreAc.attr != null) {
                    returnList.add(matches[index].getAttribute(listStoreAc.attr));
                } else {
                    returnList.add(matches[index].textContent());
                }
            }
        }

        System.out.println("[Action: StoreList] Extracted List (" + returnList.size() + " items): " + returnList);
        webReturns.storeList(listStoreAc.listName, returnList);
    }

    private void storeMap(Action.StoreMap storeMapAc) {
        System.out.println("[Action: StoreMap] Storing map to variable name: '" + storeMapAc.mapName + "' using KeySelector: '" + storeMapAc.keySelector + "' and ValueSelector: '" + storeMapAc.valueSelector + "'");
        
        Locator[] keyMatches = page.locator(storeMapAc.keySelector).all().toArray(new Locator[0]);
        Locator[] valueMatches = page.locator(storeMapAc.valueSelector).all().toArray(new Locator[0]);

        System.out.println("[Action: StoreMap] Found " + keyMatches.length + " key match(es) and " + valueMatches.length + " value match(es).");

        if (keyMatches.length == 0) {
            throw new RuntimeException("There doesn't seem to be any matches for selector '" + storeMapAc.keySelector + "'");
        }

        if (valueMatches.length == 0) {
            throw new RuntimeException("There doesn't seem to be any matches for selector '" + storeMapAc.valueSelector + "'");
        }

        if (keyMatches.length != valueMatches.length) {
            throw new RuntimeException(
                "The # of key matches (" + keyMatches.length +
                ") does not match the # of value matches (" + valueMatches.length + ")"
            );
        }

        Map<String, String> returnMap = new HashMap<>();

        if (storeMapAc.nth == null) {
            for (int i = 0; i < keyMatches.length; i++) {
                if (storeMapAc.attr != null) {
                    returnMap.put(
                        keyMatches[i].getAttribute(storeMapAc.attr),
                        valueMatches[i].getAttribute(storeMapAc.attr)
                    );
                } else {
                    returnMap.put(
                        keyMatches[i].textContent(),
                        valueMatches[i].textContent()
                    );
                }
            }
        } else {
            for (int i = 0; i < storeMapAc.nth.size(); i++) {
                int index = storeMapAc.nth.get(i);

                if (storeMapAc.attr != null) {
                    returnMap.put(
                        keyMatches[index].getAttribute(storeMapAc.attr),
                        valueMatches[index].getAttribute(storeMapAc.attr)
                    );
                } else {
                    returnMap.put(
                        keyMatches[index].textContent(),
                        valueMatches[index].textContent()
                    );
                }
            }
        }

        System.out.println("[Action: StoreMap] Extracted Map (" + returnMap.size() + " entries): " + returnMap);
        webReturns.storeMap(storeMapAc.mapName, returnMap);
    }

    private void rawStoreString(Action.RawStoreString rawStrAc) {
        System.out.println("[Action: RawStoreString] Storing raw value into string variable '" + rawStrAc.stringName + "': '" + rawStrAc.value + "'");
        if (rawStrAc.stringName == null) {
            throw new IllegalArgumentException("stringName cannot be null in RawStoreString");
        }
        webReturns.storeString(rawStrAc.stringName, rawStrAc.value);
    }

    private void rawStoreList(Action.RawStoreList rawListAc) {
        System.out.println("[Action: RawStoreList] Storing raw list into variable '" + rawListAc.listName + "': " + rawListAc.value);
        if (rawListAc.listName == null) {
            throw new IllegalArgumentException("listName cannot be null in RawStoreList");
        }
        webReturns.storeList(rawListAc.listName, rawListAc.value);
    }

    private void rawStoreMap(Action.RawStoreMap rawMapAc) {
        System.out.println("[Action: RawStoreMap] Storing raw map into variable '" + rawMapAc.mapName + "': " + rawMapAc.value);
        if (rawMapAc.mapName == null) {
            throw new IllegalArgumentException("mapName cannot be null in RawStoreMap");
        }
        webReturns.storeMap(rawMapAc.mapName, rawMapAc.value);
    }

    private void getEmail(Action.GetEmail getEmailAc) {
	
		MailBox currentMailbox = null;	
	
		for (int i = 0; i < mailboxes.size(); i++) {		
			if (mailboxes.get(i).getUsername().equals(getEmailAc.mailBox)) {
				currentMailbox = mailboxes.get(i);
			}
		}
		
		if (currentMailbox == null) {
			throw new IllegalStateException("[Action: ConnectMail]  Mailbox email address '" + getEmailAc.mailBox + "' not connected");
		}


        System.out.println("[Action: GetEmail] Fetching email in mailbox: " + currentMailbox.getUsername() + "from sender: '" + getEmailAc.fromEmail + "' using selector: '" + getEmailAc.selector + "'");
        String emailData = currentMailbox.getMessageData(getEmailAc.fromEmail, getEmailAc.selector, getEmailAc.nth);

        System.out.println("[Action: GetEmail] Email data retrieved. Storing to runtime variable '" + getEmailAc.varName + "': '" + emailData + "'");
        runtimeStorage.put(getEmailAc.varName, emailData);
    }

    private void connectToMailbox(Action.ConnectToMailBox connectMailAc) {
        System.out.println("[Action: ConnectMail] Connecting to mailbox conf:  '" + connectMailAc.mailBox + "'");

        ApplicationHome home = new ApplicationHome(WebScrape.class);
        if (home.getSource() == null) {
            throw new IllegalStateException(
                    "[Action: ConnectMail]  Unable to locate application home source path."
            );
        }

        
		Path jarDir = home.getSource().toPath().getParent();
		File mailBoxConfigFile = new File(jarDir.resolve("Mailboxes").resolve(connectMailAc.mailBox).toUri());	

		if (mailBoxConfigFile.exists()) {
			throw new IllegalStateException("[Action: ConnectMail] This mailbox configuration does not exist");
		}

		for (int i = 0; i < mailboxes.size(); i++) {
			MailBox currentMailbox = mailboxes.get(i);			
			if (currentMailbox.getUsername().equals(connectMailAc.mailBox)) {
				throw new IllegalStateException("[Action: ConnectMail]  Mailbox is already connected");
			}
		}
		
		MailBox.MailBoxConfig mailBoxConfig;
		try {
		    mailBoxConfig = new ObjectMapper().readValue(mailBoxConfigFile, MailBox.MailBoxConfig.class);
		} catch (java.io.IOException e) {
		    throw new RuntimeException("[Action: ConnectMail] Failed to parse mailbox configuration file: " + e.getMessage(), e);
		}


    }


//new MailBox("imap.zoho.com", "993", "office@tabaaperio.com", "Windows.exe@exe1", List.of("Notification"));
}
