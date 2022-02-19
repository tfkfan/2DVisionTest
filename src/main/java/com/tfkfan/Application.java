package com.tfkfan;

import com.tfkfan.gui.MainView;
import com.tfkfan.gui.Window;
import org.lwjgl.Version;

public class Application {
    public static void main(String[] args) {
        new Application().run();
    }

    public void run() {
        System.out.println("LWJGL: " + Version.getVersion());
        Window window = new Window();
        window.setView(new MainView());
        window.start();
        window.shutdown();
    }
}