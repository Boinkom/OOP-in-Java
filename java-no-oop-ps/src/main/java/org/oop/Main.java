package org.oop;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner scanner; // [Замечание] Поле scanner не инициализируется сразу, это может привести к NullPointerException, если его забыть инициализировать.
    static boolean isLoggedIn = false; // [Замечание] Возможно, стоит добавить комментарий, что переменная отвечает за статус авторизации пользователя.
    static boolean isAdministrator = false; // [Замечание] Переменная отвечает за роль администратора, можно дополнить проверку наличия роли при авторизации.
    static long loggedInUserId = 0; // [Замечание] В случае, если UserId может быть отрицательным, лучше использовать тип Long.

    public static void main(String[] args) {
        scanner = new Scanner(System.in); // [Замечание] Инициализация Scanner должна быть в try-with-resources, чтобы корректно закрывать поток ввода.
        D database = new D(); // [Замечание] Название переменной database лучше сделать более описательным, например, userDatabase.
        database.initializeDatabase();
        String choosenOption; // [Замечание] Ошибка в написании, правильнее будет chosenOption.

        while (true) { // [Замечание] Этот бесконечный цикл лучше завершать правильным условием вместо System.exit(0).
            if (!isLoggedIn) {
                // [Нарушение принципа DRY] Логику работы с меню лучше вынести в отдельные методы для уменьшения дублирования кода. Например, создание отдельного метода для вывода меню и обработки ввода пользователя.
                System.out.println("Главное меню:");
                System.out.println("1. Авторизоваться");
                System.out.println("2. Зарегистрироваться");
                System.out.println("3. Выйти");
                System.out.print("Выберите опцию: ");
                choosenOption = scanner.nextLine();
                switch (choosenOption) {
                    case "1":
                        login(database);
                        break;
                    case "2":
                        register(database);
                        break;
                    case "3":
                        System.exit(0); // [Замечание] System.exit(0) завершает приложение без учета состояния. Лучше предусмотреть другой способ завершения работы приложения.
                        break;
                    default:
                        System.out.println("Неверная опция.");
                        break;
                }
            } else {
                // [Нарушение принципа DRY] В коде дублируются блоки вывода меню и выбора опции. Это можно вынести в отдельный метод, который будет принимать список опций и возвращать выбранный пользователем пункт.
                System.out.println("Меню действий:");
                System.out.println("1. Разлогиниться");
                System.out.println("2. Создать статью");
                System.out.println("3. Посмотреть все статьи");
                System.out.println("4. Управление пользователями");
                System.out.println("5. Добавить комментарий");
                System.out.println("6. Посмотреть комментарии к статье");
                System.out.println("7. Удалить комментарий");
                System.out.println("8. Выйти");
                System.out.print("Выберите опцию: ");
                choosenOption = scanner.nextLine();
                switch (choosenOption) {
                    case "1":
                        logout();
                        break;
                    case "2":
                        addArticle(database);
                        break;
                    case "3":
                        listAllArticles(database);
                        break;
                    case "4":
                        manageUsers(database);
                        break;
                    case "5":
                        addComment(database);
                        break;
                    case "6":
                        viewComments(database);
                        break;
                    case "7":
                        deleteComment(database);
                        break;
                    case "8":
                        System.exit(0); // [Замечание] Аналогично предыдущему замечанию.
                        break;
                    default:
                        System.out.println("Неверная опция.");
                        break;
                }
            }
        }
    }

    static void printMenu(String title, List<String> options) {
        // [Рекомендация] Этот метод можно использовать для всех меню, чтобы избежать дублирования кода и соответствовать принципу DRY (Don't Repeat Yourself).
        System.out.println(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    static int getChoice(List<String> options) {
        while (true) {
            try {
                System.out.print("Enter option: ");
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= options.size()) {
                    return choice;
                } else {
                    System.out.println("Invalid option. Try again.");
                }
            } catch (NumberFormatException ex) {
                // [Замечание] Желательно логировать исключение для дальнейшей отладки.
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    static void login(D database) {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        User user = database.gubu(username); // [Замечание] Название метода gubu непонятно. Лучше использовать более очевидное название, например, getUserByUsername.

        if (user != null && user.username.equals(username)) { // [Замечание] Проверка пароля отсутствует, что критично для безопасности.
            isLoggedIn = true;
            loggedInUserId = user.id;
            isAdministrator = user.role == Role.ADMIN; // [Замечание] Проверку роли лучше вынести в отдельный метод.
            System.out.println("Успешно авторизованы.");
        } else {
            System.out.println("Неверные учетные данные."); // [Замечание] Сообщение лучше уточнить, например "Неверное имя пользователя или пароль".
        }
    }

    static void logout() {
        isLoggedIn = false;
        loggedInUserId = 0; // [Замечание] Переменная лучше именовать loggedInUserId, чтобы отразить её назначение.
        isAdministrator = false;
        System.out.println("Вы разлогинились.");
    }

    static void register(D database) {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        Role role = Role.USER; // [Замечание] Здесь предполагается, что каждый зарегистрированный пользователь получает роль USER. Администрирование нужно предусмотреть отдельно.

        User newUser = new User(0, username, password, email, role);
        if (database.cu(newUser) != null) { // [Замечание] Название метода cu неочевидно. Лучше использовать createUser или аналогичное название.
            System.out.println("Регистрация прошла успешно.");
        } else {
            System.out.println("Ошибка регистрации."); // [Замечание] Для отладки желательно уточнить ошибку.
        }
    }

    static void addArticle(D database) {
        System.out.print("Введите название статьи: ");
        String title = scanner.nextLine();
        System.out.print("Введите содержимое статьи: ");
        String content = scanner.nextLine();

        Article article = new Article(0L, title, content, loggedInUserId);
        if (database.ca(article) != null) { // [Замечание] Название метода ca неочевидно. Лучше использовать createArticle или аналогичное название.
            System.out.println("Статья успешно добавлена.");
        } else {
            System.out.println("Ошибка при добавлении статьи."); // [Замечание] Аналогично, лучше уточнить ошибку.
        }
    }

    static void listAllArticles(D database) {
        List<Article> articles = database.ga(); // [Замечание] Название метода ga неочевидно. Лучше использовать getAllArticles или аналогичное название.
        if (articles.isEmpty()) {
            System.out.println("Статьи не найдены.");
        } else {
            for (Article article : articles) {
                System.out.println("ID: " + article.id + ", Заголовок: " + article.title);
            }
        }
    }

    static void manageUsers(D database) {
        printMenu("Управление пользователями", Arrays.asList("Добавить пользователя", "Изменить пароль", "Удалить пользователя", "Посмотреть всех пользователей", "Назад"));
        int choice = getChoice(Arrays.asList("Добавить пользователя", "Изменить пароль", "Удалить пользователя", "Посмотреть всех пользователей", "Назад"));
        switch (choice) {
            case 1:
                addUser(database);
                break;
            case 2:
                changeUserPassword(database);
                break;
            case 3:
                deleteUser(database);
                break;
            case 4:
                listAllUsers(database);
                break;
            case 5:
                break; // [Замечание] Лучше явно указать, что происходит возврат в предыдущее меню.
        }
    }

    static void addUser(D database) {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        User aUser = new User(0, username, password, email, Role.USER);
        if (database.cu(aUser) != null) {
            System.out.println("Пользователь успешно добавлен!");
        } else {
            System.out.println("Ошибка при добавлении пользователя."); // [Замечание] Лучше уточнить ошибку для улучшения отладки.
        }
    }

// Рекомендации по улучшению:
// 1. Принцип DRY (Don't Repeat Yourself): В коде есть повторяющиеся блоки кода для вывода меню и обработки ввода пользователя. Эти блоки можно вынести в отдельные методы, такие как printMenu и getChoice, которые уже существуют, но их использование можно расширить. Это уменьшит дублирование кода.
// 2. Принцип KISS (Keep It Simple, Stupid): Код в методе main может быть упрощен, если разбить его на более мелкие методы. Это улучшит читаемость и поддержку кода.
// 3. Принцип YAGNI (You Aren't Gonna Need It): В коде не наблюдается явных нарушений этого принципа, однако важно избегать добавления функционала, который не используется и не требуется в данный момент.
