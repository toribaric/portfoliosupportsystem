package org.nnga.tsp.persistence.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.nnga.tsp.utility.Constant;

import javax.persistence.*;

@Entity
@Table(name = "NeuronWeight")
public class NeuronWeight implements PersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    @Column(name = "Weight")
    private double weight;
    @Transient
    private double previousWeight = 0.0;
    @Transient
    private double updateValue = Constant.UPDATE_VALUE_START.getValue();
    @Transient
    private double deltaWeight = 0.0;
    @Transient
    private double sumDerivative = 0.0;
    @Transient
    private double previousSumDerivative = 0.0;

    @ManyToOne
    @JoinColumn(name = "NeuronId")
    private Neuron neuron;

    public NeuronWeight() {

    }

    public NeuronWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPreviousWeight() {
        return previousWeight;
    }

    public void setPreviousWeight(double previousWeight) {
        this.previousWeight = previousWeight;
    }

    public double getUpdateValue() {
        return updateValue;
    }

    public void setUpdateValue(double updateValue) {
        this.updateValue = updateValue;
    }

    public double getDeltaWeight() {
        return deltaWeight;
    }

    public void setDeltaWeight(double deltaWeight) {
        this.deltaWeight = deltaWeight;
    }

    public double getSumDerivative() {
        return sumDerivative;
    }

    public void setSumDerivative(double sumDerivative) {
        this.sumDerivative = sumDerivative;
    }

    public double getPreviousSumDerivative() {
        return previousSumDerivative;
    }

    public void setPreviousSumDerivative(double previousSumDerivative) {
        this.previousSumDerivative = previousSumDerivative;
    }

    @JsonIgnore
    public Neuron getNeuron() {
        return neuron;
    }

    public void setNeuron(Neuron neuron) {
        this.neuron = neuron;
    }

}
