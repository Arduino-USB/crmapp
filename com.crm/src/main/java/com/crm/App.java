package com.crm;

import com.crm.DentrixSync;
import com.crm.NetworkMgr;
import com.crm.ConnectionLoop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.http.WebSocket;
import java.util.Map;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
		new NetworkMgr("");
		new ConnectionLoop(6741); 
    }
}
