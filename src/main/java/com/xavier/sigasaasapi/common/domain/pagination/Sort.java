package com.xavier.sigasaasapi.common.domain.pagination;
/**
 * Sort class for specifying sort order.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */

import java.util.List;

public class Sort {
    private final List<Order> orders;

    private Sort(List<Order> orders) {
        this.orders = orders;
    }

    public static Sort by(String property) {
        return new Sort(List.of(new Order(property, Direction.ASC)));
    }

    public static Sort by(Direction direction, String property) {
        return new Sort(List.of(new Order(property, direction)));
    }

    public static Sort by(Order... orders) {
        return new Sort(List.of(orders));
    }

    public List<Order> getOrders() {
        return orders;
    }

    public static class Order {
        private final String property;
        private final Direction direction;

        public Order(String property, Direction direction) {
            this.property = property;
            this.direction = direction;
        }

        public String getProperty() {
            return property;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    public enum Direction {
        ASC, DESC
    }
}

