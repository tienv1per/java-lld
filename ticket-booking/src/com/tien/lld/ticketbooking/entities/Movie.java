package com.tien.lld.ticketbooking.entities;

public final class Movie {
    private final String id;
    private final String title;
    private final String language;
    private final int durationMinutes;

    private Movie(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.language = builder.language;
        this.durationMinutes = builder.durationMinutes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", durationMinutes=" + durationMinutes +
                '}';
    }

    public static final class Builder {
        private String id;
        private String title;
        private String language;
        private int durationMinutes;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder durationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public Movie build() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("movie id is required");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("movie title is required");
            }
            if (language == null || language.trim().isEmpty()) {
                throw new IllegalArgumentException("movie language is required");
            }
            if (durationMinutes <= 0) {
                throw new IllegalArgumentException("movie durationMinutes must be positive");
            }
            return new Movie(this);
        }
    }
}

