package org.nnga.tsp.persistence.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

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
    private double previousWeight = 0;

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

    @JsonIgnore
    public Neuron getNeuron() {
        return neuron;
    }

    public void setNeuron(Neuron neuron) {
        this.neuron = neuron;
    }

}
