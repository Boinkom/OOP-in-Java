package org.oop;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner scanner;
    static boolean isLoggedIn = false;
    static boolean isAdministrator = false;
    static long loggedInUserId = 0;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        D database = new D();
        database.initializeDatabase();
        String choosenOption;

        while (true) {
            if (!isLoggedIn) {
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
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Неверная опция.");
                        break;
                }
            } else {
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
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Неверная опция.");
                        break;
                }
            }
        }
    }

    static void printMenu(String title, List<String> options) {
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
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    static void login(D database) {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        User user = database.gubu(username);

        if (user != null && user.username.equals(username)) {
            isLoggedIn = true;
            loggedInUserId = user.id;
            isAdministrator = user.role == Role.ADMIN;
            System.out.println("Успешно авторизованы.");
        } else {
            System.out.println("Неверные учетные данные.");
        }
    }

    static void logout() {
        isLoggedIn = false;
        loggedInUserId = 0;
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
        Role role = Role.USER;

        User newUser = new User(0, username, password, email, role);
        if (database.cu(newUser) != null) {
            System.out.println("Регистрация прошла успешно.");
        } else {
            System.out.println("Ошибка регистрации.");
        }
    }

    static void addArticle(D database) {
        System.out.print("Введите название статьи: ");
        String title = scanner.nextLine();
        System.out.print("Введите содержимое статьи: ");
        String content = scanner.nextLine();

        Article article = new Article(0L, title, content, loggedInUserId);
        if (database.ca(article) != null) {
            System.out.println("Статья успешно добавлена.");
        } else {
            System.out.println("Ошибка при добавлении статьи.");
        }
    }

    static void listAllArticles(D database) {
        List<Article> articles = database.ga();
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
                break;
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
            System.out.println("Ошибка при добавлении пользователя.");
        }
    }

    static void deleteUser(D database) {
        System.out.print("Введите ID пользователя для удаления: ");
        int userId = Integer.parseInt(scanner.nextLine());
        if (database.du(userId)) {
            System.out.println("Пользователь успешно удален!");
        } else {
            System.out.println("Ошибка при удалении пользователя.");
        }
    }

    static void changeUserPassword(D database) {
        System.out.print("Введите ID пользователя: ");
        int userId = Integer.parseInt(scanner.nextLine());
        System.out.print("Введите новый пароль: ");
        String newPassword = scanner.nextLine();
        if (database.cp(userId, newPassword)) {
            System.out.println("Пароль успешно изменен!");
        } else {
            System.out.println("Ошибка при изменении пароля.");
        }
    }

    static void listAllUsers(D database) {
        List<User> allUsers = database.gau();
        allUsers.forEach(u -> System.out.println("ID: " + u.id + ", Имя пользователя: " + u.username));
    }

    static void addComment(D database) {
        System.out.print("Введите ID статьи: ");
        Long articleId = Long.parseLong(scanner.nextLine());
        System.out.print("Введите ваш комментарий: ");
        String content = scanner.nextLine();

        Comment comment = new Comment(0L, articleId, loggedInUserId, content);
        if (database.cc(comment) != null) {
            System.out.println("Комментарий добавлен!");
        } else {
            System.out.println("Ошибка при добавлении комментария.");
        }
    }

    static void viewComments(D database) {
        System.out.print("Введите ID статьи: ");
        Long articleId = Long.parseLong(scanner.nextLine());

        List<Comment> comments = database.gc(articleId);
        if (comments.isEmpty()) {
            System.out.println("Комментариев к статье нет.");
        } else {
            comments.forEach(comment -> System.out.println("ID: " + comment.id + ", Содержание: " + comment.content));
        }
    }

    static void deleteComment(D database) {
        System.out.print("Введите ID комментария для удаления: ");
        Long commentId = Long.parseLong(scanner.nextLine());

        if (database.dc(commentId)) {
            System.out.println("Комментарий удален.");
        } else {
            System.out.println("Ошибка при удалении комментария.");
        }
    }
}
