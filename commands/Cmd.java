package me.alithernyx.bot.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cmd {

    /**
     * Provides the command class with the headers
     *
     * @see ICommand#alias()
     *
     * @return Command headers
     */
    String[] alias();

    /**
     * Provides the command class with the description
     *
     * @see ICommand#description()
     *
     * @return Command description
     */
    String description();

    /**
     * Provides the command class with the syntax
     *
     * @see ICommand#syntax()
     *
     * @return Command syntax
     */
    String[] syntax() default {};

    /**
     * Provides the command class with the syntax
     *
     * @see ICommand#reqArgs()
     *
     * @return Command reqArgs
     */
    boolean reqArgs() default false;

    boolean receiveMsgs() default false;
}