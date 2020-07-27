package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
        List<UserMealWithExcess> result = new ArrayList<>();
        List<UserMeal> mealsToday = new ArrayList<>();
        int curDay = 0;
        int caloriesToday = 0;
        for (UserMeal meal : meals) {
            LocalTime mealLocalTime = meal.getDateTime().toLocalTime();
            if (mealLocalTime.isAfter(endTime) || mealLocalTime.isBefore(startTime)) {
                continue;
            }

            int mealDay = meal.getDateTime().getDayOfYear();
            if (curDay == 0) {
                curDay = mealDay;
                caloriesToday = meal.getCalories();
                mealsToday.add(meal);
            } else if (mealDay == curDay) {
                caloriesToday += meal.getCalories();
                mealsToday.add(meal);
            } else {
                result.addAll(getUserExcessMeals(mealsToday, caloriesToday > caloriesPerDay));
                mealsToday.clear();
                curDay = mealDay;
                caloriesToday = meal.getCalories();
                mealsToday.add(meal);
            }
        }
        if (!mealsToday.isEmpty()) {
            result.addAll(getUserExcessMeals(mealsToday, caloriesToday > caloriesPerDay));
        }
        return result;
    }

    private static Collection<UserMealWithExcess> getUserExcessMeals(List<UserMeal> mealsToday, boolean excess) {
        List<UserMealWithExcess> result = new ArrayList<>();
        mealsToday.forEach(m -> result.add(
                new UserMealWithExcess(m.getDateTime(),
                        m.getDescription(), m.getCalories(), excess)));
        return result;
    }

    public static List<UserMealWithExcess> filteredUsingStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        var daysToCalories = meals.stream()
                .collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream().filter(meal -> meal.getDateTime().toLocalTime().isBefore(endTime)
                && meal.getDateTime().toLocalTime().isAfter(startTime))
                .map(m -> new UserMealWithExcess(m.getDateTime(),
                        m.getDescription(), m.getCalories(),
                        daysToCalories.get(m.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
