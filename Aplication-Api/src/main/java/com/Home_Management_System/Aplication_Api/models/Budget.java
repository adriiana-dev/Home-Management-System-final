package com.Home_Management_System.Aplication_Api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Document(collection = "budgets")
public class Budget {
    @Id
    private String id;
    private String month;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal limitAmount;

    public Budget() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public void setLimitAmount(BigDecimal limitAmount) { this.limitAmount = limitAmount; }
}