package org.nnga.tsp.persistence.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "NeuralNetwork")
public class NeuralNetwork implements PersistenceEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String description;
    @Column(name = "NumInputNeurons")
    private int numInputNeurons;
    @Column(name = "NumOutputNeurons")
    private int numOutputNeurons;
    @Column(name = "NumHiddenLayers")
    private int numHiddenLayers;
    @Column(name = "ActivationFunction")
    private String activationFunction;
    @Column(name = "Trained")
    private boolean trained;

    @OneToMany(mappedBy = "neuralNetwork", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @OrderBy("id ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<NeuronLayer> neuronLayers;

    public NeuralNetwork() {
        neuronLayers = new ArrayList<NeuronLayer>();
        this.trained = false;
    }

    public NeuralNetwork(String name, String description, int numInputNeurons, int numOutputNeurons, int numHiddenLayers, String activationFunction, List<NeuronLayer> neuronLayers) {
        this.name = name;
        this.description = description;
        this.numInputNeurons = numInputNeurons;
        this.numOutputNeurons = numOutputNeurons;
        this.numHiddenLayers = numHiddenLayers;
        this.activationFunction = activationFunction;
        this.trained = false;
        for( NeuronLayer neuronLayer : neuronLayers ) {
            neuronLayer.setNeuralNetwork(this);
        }
        this.neuronLayers = neuronLayers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumInputNeurons() {
        return numInputNeurons;
    }

    public void setNumInputNeurons(int numInputNeurons) {
        this.numInputNeurons = numInputNeurons;
    }

    public int getNumOutputNeurons() {
        return numOutputNeurons;
    }

    public void setNumOutputNeurons(int numOutputNeurons) {
        this.numOutputNeurons = numOutputNeurons;
    }

    public int getNumHiddenLayers() {
        return numHiddenLayers;
    }

    public void setNumHiddenLayers(int numHiddenLayers) {
        this.numHiddenLayers = numHiddenLayers;
    }

    public String getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(String activationFunction) {
        this.activationFunction = activationFunction;
    }

    public boolean isTrained() {
        return trained;
    }

    public void setTrained(boolean trained) {
        this.trained = trained;
    }

    public List<NeuronLayer> getNeuronLayers() {
        return neuronLayers;
    }

    public void setNeuronLayers(List<NeuronLayer> neuronLayers) {
        for( NeuronLayer neuronLayer : neuronLayers ) {
            neuronLayer.setNeuralNetwork(this);
        }
        this.neuronLayers = neuronLayers;
    }

    public void addNeuronLayer(NeuronLayer neuronLayer) {
        neuronLayer.setNeuralNetwork(this);
        neuronLayers.add(neuronLayer);
    }

}
