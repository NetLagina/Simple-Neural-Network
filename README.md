# Simple-Neural-Network
Simple neural network with backpropagation on java

The neural network can learn to recognize the numbers 0 - 9 in the form of an electronic scoreboard. For example:

    1 1 1
    1 0 1
    1 1 1
    1 0 1
    1 1 1 - number 8.
    
The main.java file is configured with an example of network training and an attempt to recognize the number 2.
## Prepairing trainsets
A training file is required for network training. You can specify a file and create a trainset like this:

    var fileLoader = new TrainSetsLoader (".\\trainSet.txt");
    List<TrainSet> trainSets = fileLoader.loadTrainSets();

The trainset file should look like: 
* A string of input parameters, separated by spaces. Input parameters can be integers or fractional numbers. 
* A string of expected output values. May consist of integers or fractional numbers. 

The lines are repeated as many times as the sets are prepared.

trainSet.txt - example of trainset file.

## Neural Network
Next, you need to create a neural network object.

    var network = NeuralNetworkFactory.getNeuralNetwork(new int[]{15, 15, 10}, true);
     
The numbers in the array indicate the number of neurons in each layer, starting from the input layer. In our case, the numbers consist of 15 points, which can be shaded (included) or not. The output values are 10 numbers, each of which denotes digits from 0 to 9. The closer the digit is to "1", the more the neural network is confident in the correctness of this option.
## Training
The reinforcement training is performed as follows:

    var teacher = new Teacher(network, trainSets);
    teacher.teachBackpropagation();

## Saving graph weights (optional)
After training, you may need to save the results of the calculation of the graph weights, if you are going to restart the program with the same weights. To do this, use the WeightsSaver class. It creates the file weights.json in the root directory of the application.

    WeightsSaver ws = new WeightsSaver();
    ws.save(network.getWeights());

## Calculating results
To use a neural network on arbitrary input data, you should set it as an input layer, and start the calculation process.

    network.setInputLayer(new NeuralLayerImpl(new Neuron[]{
                new InputNeuronImpl(1), new InputNeuronImpl(1), new InputNeuronImpl(1),
                new InputNeuronImpl(0), new InputNeuronImpl(0), new InputNeuronImpl(1),
                new InputNeuronImpl(1), new InputNeuronImpl(1), new InputNeuronImpl(1),
                new InputNeuronImpl(1), new InputNeuronImpl(0), new InputNeuronImpl(0),
                new InputNeuronImpl(1), new InputNeuronImpl(1), new InputNeuronImpl(1)
        }));
		
    network.calc();
    
Output:

    var output = network.getOutputLayer();
    System.out.println("Answer is");
    for (var i = 0; i < output.size(); i++) {
        System.out.printf("%d: %.1f%%%n", ((i + 1) % 10), output.getNeuron(i).getValue() * 100);
    }
