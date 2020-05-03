package com.m3u.parser.service.filter;

import com.m3u.parser.controller.model.M3UMetaSearch;
import io.reactivex.functions.Predicate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryFilter implements Predicate<M3UMetaSearch> {

    private Set<String> categoryUpperCase;

    public static CategoryFilter filterCategory(Set<String> categories) {
        Set<String> catUpper = categories.stream().map(String::toUpperCase).collect(Collectors.toSet());
        return new CategoryFilter(catUpper);
    }

    @Override
    public boolean test(M3UMetaSearch m3UMetaSearch) throws Throwable {
        return categoryUpperCase.isEmpty() || categoryUpperCase.contains(m3UMetaSearch.getGroupId().toUpperCase());
    }
}
