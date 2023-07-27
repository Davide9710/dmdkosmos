package com.example.test.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RemoveEntity {
    private String transactionId;

    public RemoveEntity(){}
}
