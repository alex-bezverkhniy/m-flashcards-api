package com.alexbezverkhniy.myflashcards.common;

import java.time.LocalDateTime;

public interface Model {
    Long getId();

    void setId(Long id);

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getCreatedBy();

    String getUpdatedBy();

    void setCreatedAt(LocalDateTime createdAt);

    void setUpdatedAt(LocalDateTime updatedAt);

    void setCreatedBy(String createdBy);

    void setUpdatedBy(String updatedBy);
}