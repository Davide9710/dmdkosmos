package com.example.test.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

//クラスの全てのフィールドを引数にとる。
@AllArgsConstructor
@Data
public class TransactionEntity {
    private String toAccountId;
    private long amount;

    public TransactionEntity(){}
}