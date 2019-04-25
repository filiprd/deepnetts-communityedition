/**
 *  DeepNetts is pure Java Deep Learning Library with support for Backpropagation
 *  based learning and image recognition.
 *
 *  Copyright (C) 2017  Zoran Sevarac <sevarac@gmail.com>
 *
 * This file is part of DeepNetts.
 *
 * DeepNetts is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <https://www.gnu.org/licenses/>.package
 * deepnetts.core;
 */
package deepnetts.examples;

import deepnetts.core.DeepNetts;
import deepnetts.data.ImageSet;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.DeepNettsException;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import deepnetts.eval.PerformanceMeasure;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.util.FileIO;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Example of training Convolutional network for MNIST data set. Note: in order
 * to run this example you must download mnist data set and update image paths
 * in train.txt file
 *
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public class MnistSvi {

    int imageWidth = 28;
    int imageHeight = 28;

    //String labelsFile   = "/home/zoran/datasets/mnist/train/labels.txt";
    String labelsFile = "D:\\datasets\\mnist\\train\\labels.txt";
    //   String trainingFile = "datasets/mnist/train2.txt"; // 1000 cifara - probaj sa 10 000
    //     String trainingFile = "/home/zoran/datasets/mnist/train/train.txt"; // 1000 cifara - probaj sa 10 000
    String trainingFile = "D:\\datasets\\mnist\\train\\train.txt"; // 1000 cifara - probaj sa 10 000

    private static final Logger LOGGER = LogManager.getLogger(DeepNetts.class.getName());

    public void run() throws DeepNettsException, IOException {

        LOGGER.info("Training convolutional network with MNIST data set");
        LOGGER.info("Creating image data set...");

        // create a data set from images and labels
        ImageSet imageSet = new ImageSet(imageWidth, imageHeight);
        imageSet.loadLabels(new File(labelsFile));
        imageSet.loadImages(new File(trainingFile), 5000); //50000
        imageSet.invert();
     //   imageSet.zeroMean();
        imageSet.shuffle();

        imageSet.countByClasses();      
        
        ImageSet[] imageSets = imageSet.split(0.7, 0.3);
        int labelsCount = imageSet.getLabelsCount();

        LOGGER.info("------------------------------------------------");
        LOGGER.info("CREATING NEURAL NETWORK");
        LOGGER.info("------------------------------------------------");

        // create convolutional neural network architecture
        ConvolutionalNetwork neuralNet = ConvolutionalNetwork.builder()
                .addInputLayer(imageWidth, imageHeight, 3)
                .addConvolutionalLayer(5, 5, 4)
              //  .addMaxPoolingLayer(2, 2)
                .addConvolutionalLayer(3, 3, 8)
//                .addMaxPoolingLayer(2, 2)
              //  .addFullyConnectedLayer(40)
              //  .addFullyConnectedLayer(30)
                .addFullyConnectedLayer(20)
                .addOutputLayer(labelsCount, ActivationType.SOFTMAX)
                .hiddenActivationFunction(ActivationType.RELU)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(123)
                .build();
        
        LOGGER.info(neuralNet);        
        
        // create a trainer and train network
        BackpropagationTrainer trainer = new BackpropagationTrainer(neuralNet);
        trainer.setLearningRate(0.01f)
                .setMomentum(0.9f)
                .setMaxError(0.05f)
               // .setMaxEpochs(1)
                .setBatchMode(false)
                //.setBatchSize(50)
                .setOptimizer(OptimizerType.SGD);
        trainer.train(imageSets[0]);
        
        // Epoch:1, Time:268816ms, TrainError:2.2856517, TrainErrorChange:2.2856517, TrainAccuracy: 0.0

        // Test trained network
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        PerformanceMeasure pm = evaluator.evaluatePerformance(neuralNet, imageSets[1]);
        LOGGER.info("------------------------------------------------");
        LOGGER.info("Classification performance measure" + System.lineSeparator());
        LOGGER.info("TOTAL AVERAGE");
        LOGGER.info(evaluator.getTotalAverage());
        LOGGER.info("By Class");
        Map<String, PerformanceMeasure> byClass = evaluator.getPerformanceByClass();
        byClass.entrySet().stream().forEach((entry) -> {
            LOGGER.info("Class " + entry.getKey() + ":");
            LOGGER.info(entry.getValue());
            LOGGER.info("----------------");
        });
        
         LOGGER.info("CONFUSION MATRIX");
        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        LOGGER.info(cm);

        // Save network to file as json
        //FileIO.writeToFile(neuralNet, "mnistDemo.dnet");
        FileIO.writeToFileAsJson(neuralNet, "mnistDemo.json");
    }

    public static void main(String[] args) throws IOException {
        (new MnistSvi()).run();
    }


}