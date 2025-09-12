package com.xavier.sigasaasapi.common.domain.valueobject;

import java.util.Objects;

/**
 * Address value object representing a physical address.
 * Supports Mozambican address format.
 * Ensures immutability and validates required fields.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */

public class Address {
    private final String street;
    private final String neighborhood;
    private final String city;
    private final String province;
    private final String postalCode;
    private final String country;

    private Address(Builder builder) {
        this.street = builder.street;
        this.neighborhood = builder.neighborhood;
        this.city = builder.city;
        this.province = builder.province;
        this.postalCode = builder.postalCode;
        this.country = builder.country;

        validate();
    }

    private void validate() {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
    }

    public String getStreet() {
        return street;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();

        if (street != null && !street.isEmpty()) {
            sb.append(street).append(", ");
        }
        if (neighborhood != null && !neighborhood.isEmpty()) {
            sb.append(neighborhood).append(", ");
        }
        sb.append(city);
        if (province != null && !province.isEmpty()) {
            sb.append(", ").append(province);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        sb.append(", ").append(country);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(neighborhood, address.neighborhood) &&
                Objects.equals(city, address.city) &&
                Objects.equals(province, address.province) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, neighborhood, city, province, postalCode, country);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String street;
        private String neighborhood;
        private String city;
        private String province;
        private String postalCode;
        private String country = "Moçambique";

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder neighborhood(String neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder province(String province) {
            this.province = province;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}
