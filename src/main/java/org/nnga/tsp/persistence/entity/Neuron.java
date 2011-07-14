package org.nnga.tsp.persistence.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Neuron")
public class Neuron implements PersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    @Column(name = "NumInputs")
    private int numInputs;
    @Column(name = "Activation")
    private double activation;
    @Column(name = "Error")
    private double error;

    @ManyToOne
    @JoinColumn(name = "NeuronLayerId")
    private NeuronLayer neuronLayer;

    @OneToMany(mappedBy = "neuron", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @OrderBy("id ASC")
    @Fetch(FetchMode.SUBSELECT)
    private List<NeuronWeight> neuronWeights;

    public Neuron() {

    }

    public Neuron(int numInputs, List<NeuronWeight> neuronWeights) {
        this.numInputs = numInputs;
        for( NeuronWeight neuronWeight : neuronWeights ) {
            neuronWeight.setNeuron(this);
        }
        this.neuronWeights = neuronWeights;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumInputs() {
        return numInputs;
    }

    public void setNumInputs(int numInputs) {
        this.numInputs = numInputs;
    }

    public double getActivation() {
        return activation;
    }

    public void setActivation(double activation) {
        this.activation = activation;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    @JsonIgnore
    public NeuronLayer getNeuronLayer() {
        return neuronLayer;
    }

    public void setNeuronLayer(NeuronLayer neuronLayer) {
        this.neuronLayer = neuronLayer;
    }

    public List<NeuronWeight> getNeuronWeights() {
        return neuronWeights;
    }

    public void setNeuronWeights(List<NeuronWeight> neuronWeights) {
        for( NeuronWeight neuronWeight : neuronWeights ) {
            neuronWeight.setNeuron(this);
        }
        this.neuronWeights = neuronWeights;
    }

}
