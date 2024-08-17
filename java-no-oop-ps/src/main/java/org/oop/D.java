package org.oop;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class D {

    // Принцип KISS нарушен: Этот метод выполняет две разные задачи - инициализацию базы данных и вставку данных.
    // Рекомендация: Разделить на два отдельных метода для упрощения кода и улучшения его читаемости.
    public void initializeDatabase() {
        {
            String[] initializationQueries = new String[]{
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id SERIAL PRIMARY KEY," +
                            "username VARCHAR(50) NOT NULL," +
                            "password VARCHAR(255) NOT NULL," +
                            "email VARCHAR(100) NOT NULL," +
                            "role VARCHAR(50) NOT NULL DEFAULT 'USER')",

                    "CREATE TABLE IF NOT EXISTS articles (" +
                            "id SERIAL PRIMARY KEY," +
                            "title VARCHAR(255) NOT NULL," +
                            "content TEXT NOT NULL," +
                            "author_id INTEGER," +
                            "FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE);",

                    "CREATE TABLE IF NOT EXISTS comments (" +
                            "id SERIAL PRIMARY KEY," +
                            "article_id INTEGER," +
                            "user_id INTEGER," +
                            "comment_text TEXT NOT NULL," +
                            "FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE," +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);" +

                            "INSERT INTO users (username, password, email, role) " +
                            "SELECT 'admin', '$2a$10$ELqr66UvJgnkkN9e6hrYGO.brljJ//Y2MTpMpVfhdmgEUB0wmS2cC', 'admin@example.com', 'ADMIN' " +
                            "WHERE NOT EXISTS (" +
                            "SELECT * FROM users WHERE username = 'admin'" +
                            ");"
            };

            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/oopshop", "exampleuser", "examplepass");
                 Statement stmt = conn.createStatement()) {
                // Принцип DRY нарушен: Повторение кода установки соединения с базой данных и выполнения запросов.
                // Рекомендация: Вынести эти операции в отдельный метод.
                for (String sql : initializationQueries) {
                    stmt.execute(sql);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public Article ca(Article article) {
        String query = "INSERT INTO articles (title, content, author_id) VALUES (?, ?, ?)";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Принцип DRY нарушен: Повторение кода подготовки и выполнения запросов, аналогичный код встречается в других методах.
            // Рекомендация: Вынести общую логику в отдельный метод.
            preparedStatement.setString(1, article.title);
            preparedStatement.setString(2, article.content);
            preparedStatement.setLong(3, article.authorId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating article failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.id = generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating article failed, no ID obtained.");
                }
            }

            return article;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Article gai(long id) {
        String query = "SELECT id, title, content, author_id FROM articles WHERE id = ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Article(
                            resultSet.getLong("id"),
                            resultSet.getString("title"),
                            resultSet.getString("content"),
                            resultSet.getLong("author_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Article> ga(String title) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT id, title, content, author_id FROM articles WHERE title LIKE ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + title + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    articles.add(new Article(
                            resultSet.getLong("id"),
                            resultSet.getString("title"),
                            resultSet.getString("content"),
                            resultSet.getLong("author_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public List<Article> ga() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT id, title, content, author_id FROM articles";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Article article = new Article(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getLong("author_id")
                );
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public boolean ua(Article article) {
        String query = "UPDATE articles SET title = ?, content = ?, author_id = ?, WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, article.title);
            preparedStatement.setString(2, article.content);
            preparedStatement.setLong(3, article.id);
            preparedStatement.setLong(4, article.authorId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean da(long id) {
        String query = "DELETE FROM articles WHERE id = ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User cu(User user) {
        // Принцип DRY нарушен: Повторение кода подготовки и выполнения запросов.
        // Рекомендация: Вынести общую логику в отдельный метод.

        // Принцип SRP нарушен: Прямое соединение с базой данных в этом методе нарушает принцип единственной ответственности.
        // Рекомендация: Вынести логику работы с базой данных в отдельный класс.
        user.password = hashPassword(user.password);

        String query = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/oopshop", "exampleuser", "examplepass");
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.username);
            preparedStatement.setString(2, user.password);
            preparedStatement.setString(3, user.email);
            preparedStatement.setString(4, user.role.toString());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Создание пользователя не удалось, изменений не произошло.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.id = generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Создание пользователя не удалось, ID не был получен.");
                }
            }

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User gu(String username) {
        String query = "SELECT id, username, password, email, role FROM users WHERE username = ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("email"),
                            User.Role.valueOf(resultSet.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean uu(User user) {
        String query = "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.username);
            preparedStatement.setString(2, user.password);
            preparedStatement.setString(3, user.email);
            preparedStatement.setString(4, user.role.toString());
            preparedStatement.setLong(5, user.id);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean du(long id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Принцип YAGNI нарушен: В проекте используется управление комментариями через базу данных, а данный метод работает с комментариями в памяти.
    // Рекомендация: Удалить данный метод, чтобы избежать избыточности и путаницы.
    public List<Comment> gc(Article article) {
        return article.comments;
    }

    // Принцип YAGNI нарушен: В проекте используется управление комментариями через базу данных, а данный метод работает с комментариями в памяти.
    // Рекомендация: Удалить данный метод, чтобы избежать избыточности и путаницы.
    public void dc(Article article, Comment comment) {
        article.comments.remove(comment);
    }

    public static class Article {
        public Long id;
        public String title;
        public String content;
        public Long authorId;
        public List<Comment> comments;

        public Article(Long id, String title, String content, Long authorId) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.authorId = authorId;
            this.comments = new ArrayList<>();
        }
    }

    public static class Comment {
        public Long id;
        public Long articleId;
        public Long userId;
        public String commentText;

        public Comment(Long id, Long articleId, Long userId, String commentText) {
            this.id = id;
            this.articleId = articleId;
            this.userId = userId;
            this.commentText = commentText;
        }
    }

    public static class User {
        public Long id;
        public String username;
        public String password;
        public String email;
        public Role role;

        public enum Role {
            USER,
            ADMIN
        }

        public User(Long id, String username, String password, String email, Role role) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.email = email;
            this.role = role;
        }
    }
}
