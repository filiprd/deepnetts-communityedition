/**
 *  DeepNetts is pure Java Deep Learning Library with support for Backpropagation
 *  based learning and image recognition.
 *
 *  Copyright (C) 2017  Zoran Sevarac <sevarac@gmail.com>
 *
 *  This file is part of DeepNetts.
 *
 *  DeepNetts is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.package deepnetts.core;
 */

package deepnetts.examples;

import deepnetts.data.ExampleImage;
import deepnetts.net.ConvolutionalNetwork;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.visrec.AbstractImageClassifier;
import javax.visrec.ml.classification.Classifier;

/**
 * Example how to create image classifier using ConvolutionalNetwork
 *
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public class DeepNettsImageClassifier extends AbstractImageClassifier<BufferedImage, ConvolutionalNetwork> {

    private ConvolutionalNetwork convNet;

    public DeepNettsImageClassifier(ConvolutionalNetwork convNet) {
      //TODO: has to beable to specify model instance also
       super(BufferedImage.class, convNet);
     // super(convNet);
    }

    /**
     *
     * @param image
     * @return
     */
    //@Override
    public Map<String, Float> classify(BufferedImage image) {
        HashMap<String, Float> results = new HashMap<>();
        ConvolutionalNetwork convNet = getModel();

       // convNet.setInput((new ExampleImage(image)).getInput());
        convNet.forward();
        float[] outputs = convNet.getOutput();

        //     float max = outputs[0];
//       int maxIdx = 0;
        for (int i = 1; i < outputs.length; i++) {
//           if (outputs[i] > max) {
//               max = outputs[i];
//               maxIdx = i;
//           }
            results.put(convNet.getOutputLabel(i), outputs[i]);
        }

        return results;
    }

    @Override
     public Map<String, Float> classify(File imageFile) throws IOException {
         BufferedImage image = ImageIO.read(imageFile);
         return classify(image);
     }



    // getTopKTags()
//    @Override
//    public List<RecognitionResult> recognize(BufferedImage image, int k) {
//        List<RecognitionResult> results = new ArrayList<>();
//
//       convNet.setInput((new ExampleImage(image)).getInput());
//       convNet.forward();
//       float[] outputs = convNet.getOutput();
//
//       // iterate outputs and ket k highest with labels - kako naci top k vrednosti - prouci algoritam!!!
//       float max = outputs[0];
//       int maxIdx = 0;
//       for(int i=1; i<outputs.length; i++) {
//           if (outputs[i] > max) {
//               max = outputs[i];
//               maxIdx = i;
//           }
//       }
//
//       RecognitionResult result = new RecognitionResult(convNet.getOutputLabel(maxIdx), max);
//       results.add(result);
//
//       return results;
//    }

//    @Override
//    public Classifier build(Properties prop) {
//        // build classifier from specified propertiers
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}