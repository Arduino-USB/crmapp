package com.crm.webscrape;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.Map;

//all nth and expect can be null

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    include = JsonTypeInfo.As.WRAPPER_OBJECT
)


public class Action {


    public static class Navigate extends Action {
        public String url;
    }

	public static class Click extends Action {
        public String selector;
        public Integer expect;

        public List<Integer> nth;
	}

	public static class FillNormal extends Action {
        public String selector;
		public String text;	

        public Integer expect;
        public List<Integer> nth;
	}

    public static class FillVar extends Action {
        public String selector;
		public String varName;	

        public Integer expect;
        public List<Integer> nth;
	}

    
    public static class SelectNormal extends Action {
        public String selector;
        public String optionString;

        public Integer expect;
        public List<Integer> nth;
    }

    public static class SelectVar extends Action {
        public String selector;
        public String optionVarName;

        public Integer expect;
        public List<Integer> nth;
    }
    
    
    public static class Wait extends Action {
        public Integer timeout;
    }
    
    public static class WaitFor extends Action {
        public String selector;

        public Integer timeout; //can be null

        public Integer atLeast;
    }

    public static class Conditional extends Action {
        public String selector;
        public Action ifAction;
        public Action elseAction;
    }

    public static class StoreString extends Action {
        public String selector;
        public String stringName;

        public String attr; // if null does textContent
        public Integer nth;
    }

    public static class StoreList extends Action {
        public String selector;
        public String listName;
        public String attr; // if null does textContent

        public Integer expect;

        public List<Integer> nth; // if null then does all intervales esle it does if (i % anythoing in the list == 0)
    }

    public static class StoreMap extends Action {
        public String keySelector;
        public String valueSelector;

        public String mapName;
        public String attr; // if null does textContent

        public List<Integer> nth; // if null then does all intervales else it does if (i % anythoing in the list == 0)
    
    }


    public static class RawStoreString extends Action {
        public String stringName;
        public String value;
    }

    public static class RawStoreList extends Action {
        public String listName;
        public List<String> value;
    }

    public static class RawStoreMap extends Action {
        public String mapName;
        public Map<String, String> value;
    }

    public static class GetEmail extends Action {
        public String fromEmail;

        public String selector;
        public Integer nth;

        public String varName;
    }
    


}
