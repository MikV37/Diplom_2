package ru.stellarburgers.api.data;

import com.github.javafaker.Faker;

public class UserGenerator {

    private static final Faker faker = new Faker();

    public static User createRandomUser() {
        return new User(faker.internet().emailAddress(), faker.internet().password(8, 16), faker.name().firstName());
    }

    public static User createUserWithoutEmail() {
        return new User(null, faker.internet().password(8, 16), faker.name().firstName());
    }

    public static User createUserWithoutPassword() {
        return new User(faker.internet().emailAddress(), null, faker.name().firstName());
    }

    public static User createUserWithoutName() {
        return new User(faker.internet().emailAddress(), faker.internet().password(8, 16), null);
    }
}