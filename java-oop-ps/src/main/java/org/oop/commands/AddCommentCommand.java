package org.oop.commands;

import org.oop.api.ICommentService;
import org.oop.api.ICommand;
import org.oop.commands.menu.BaseCommand;
import org.oop.commands.menu.UserMenu;
import org.oop.di.Injector;
import org.oop.model.Comment;

import java.util.Optional;

public class AddCommentCommand extends BaseCommand {
    private final ICommentService commentService;

    public AddCommentCommand() {
        this.commentService = Injector.getInstance().getService(ICommentService.class);
    }

    @Override
    public ICommand execute() {
        ioService.printLine("Добавление комментария к статье:");

        Optional<String> articleId = promptOrReturn("Введите ID статьи:");
        if (articleId.isEmpty()) return new UserMenu();

        Optional<String> username = promptOrReturn("Введите ваше имя пользователя:");
        if (username.isEmpty()) return new UserMenu();

        Optional<String> content = promptOrReturn("Введите текст комментария:");
        if (content.isEmpty()) return new UserMenu();

        Comment comment = new Comment(username.get(), content.get());
        boolean isAdded = commentService.addComment(articleId.get(), comment);

        if (isAdded) {
            ioService.printLine("Комментарий успешно добавлен.");
        } else {
            ioService.printLine("Не удалось добавить комментарий.");
        }

        return new UserMenu();
    }

    @Override
    public String getDescription() {
        return "Добавить комментарий к статье";
    }

    private Optional<String> promptOrReturn(String message) {
        String input = ioService.prompt(message);
        if ("back".equalsIgnoreCase(input)) {
            return Optional.empty();
        }
        return Optional.of(input);
    }
}
