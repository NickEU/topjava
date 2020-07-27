package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Breakfast", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 11, 0), "Lunch", 1100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Dinner", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Breakfast", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Lunch", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Dinner", 410)
        );

        List<UserMealWithExcess> mealsTo
                = filteredUsingLoops(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println("---");
        filteredUsingStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredUsingLoops(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> daysToCalories = new HashMap<>();
        for (UserMeal meal : meals) {
            var date = meal.getDateTime().toLocalDate();
            daysToCalories.put(date, daysToCalories.getOrDefault(date, 0) + meal.getCalories());
        }

        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal m : meals) {
            var date = m.getDateTime();
            if (TimeUtil.isBetweenInclusive(date.toLocalTime(), startTime, endTime)) {
                result.add(
                        new UserMealWithExcess(m.getDateTime(),
                                m.getDescription(),
                                m.getCalories(),
                                daysToCalories.get(date.toLocalDate()) > caloriesPerDay));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredUsingStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        var daysToCalories = meals.stream()
                .collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream().filter(meal
                -> TimeUtil.isBetweenInclusive(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> new UserMealWithExcess(m.getDateTime(),
                        m.getDescription(), m.getCalories(),
                        daysToCalories.get(m.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
