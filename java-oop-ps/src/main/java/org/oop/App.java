package org.oop;

import org.oop.api.IDatabaseService;
import org.oop.api.ICommand;
import org.oop.commands.menu.MainMenu;
import org.oop.di.Injector;

public class App {

    private static App instance;

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    private ICommand command;

    App() {
        this.command = new MainMenu();

        IDatabaseService databaseService = Injector.getInstance().getService(IDatabaseService.class);
        databaseService.initializeDatabase();
    }

    public void run() {
        while (command != null) {
            command = command.execute();
        }
    }
}
