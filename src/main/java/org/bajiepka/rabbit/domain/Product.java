package org.bajiepka.rabbit.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "product")
public class Product {

    private String name;
    private String description;
    private Double cost;
    private Integer weight;

    public Product() {
    }

    public Product(String name, String description, Double cost, Integer weight) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.weight = weight;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlAttribute(name = "cost")
    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    @XmlAttribute(name = "weight")
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
