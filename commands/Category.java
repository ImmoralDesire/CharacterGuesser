package me.alithernyx.bot.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Category {

    /**
     * @return The name of the Category
     */
    String name();

    /**
     * Default Category
     */
    @Category(name = "Default") class Default {}
}