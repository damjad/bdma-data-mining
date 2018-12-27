/*******************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package smile.demo.clustering;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.danish.world.dm.utils.HaversineDistance;
import com.danish.world.dm.utils.CSVWriter;
import smile.clustering.DENCLUE;
import smile.plot.Palette;
import smile.plot.PlotCanvas;
import smile.plot.ScatterPlot;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class DENCLUEDemo  extends ClusteringDemo {
    JTextField kField;
    JTextField sigmaField;
    int k = 20;
    double sigma = 1;

    double distanceMeters = 20000.0d;
    int minPts = 50;

    HaversineDistance haversineDistance = new HaversineDistance();
    String outputFile = "/mnt/1717A37A1971CE02/WorkSpaces/BDMA/data-mining/bdma-data-mining/db-scan-lab/data/output_denclue.csv";


    public DENCLUEDemo() {
        // Remove K TextFile
        optionPane.remove(optionPane.getComponentCount() - 1);

        kField = new JTextField(Integer.toString(k), 5);
        optionPane.add(kField);

        sigmaField = new JTextField(Double.toString(sigma), 5);
        optionPane.add(new JLabel("Sigma:"));
        optionPane.add(sigmaField);
    }

    @Override
    public JComponent learn() {
        try {
            k = Integer.parseInt(kField.getText().trim());
            if (k < 1) {
                JOptionPane.showMessageDialog(this, "Invalid K: " + k, "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid K: " + kField.getText(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            sigma = Double.parseDouble(sigmaField.getText().trim());
            if (sigma <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid Sigma: " + sigma, "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Sigma: " + sigmaField.getText(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        long clock = System.currentTimeMillis();
        DENCLUE denclue = new DENCLUE(dataset[datasetIndex], sigma, k);
        List<List<String>> csvWritableData = new ArrayList<>(dataset[datasetIndex].length);
        Map<Integer, Boolean> attractivePointsMask = new HashMap<>();

        System.out.format("DENCLUE clusterings %d samples in %dms\n", dataset[datasetIndex].length, System.currentTimeMillis()-clock);

        JPanel pane = new JPanel(new GridLayout(1, 2));

        double[][] arr = new double[1][2];
        arr[0][0]= -3;
        arr[0][1]= 50;

        PlotCanvas plot = ScatterPlot.plot(arr);
        //plot.clear();

        System.out.format("Number of clusters: %d\n", denclue.getNumClusters());
        for (int l = 0; l < denclue.getNumClusters(); l++) {
            List<double[]> clusterPoints = new ArrayList<>();
            for (int i = 0, j = 0; i < dataset[datasetIndex].length; i++) {

                if (denclue.getClusterLabel()[i] == l && nearADensityAttractor(attractivePointsMask, dataset[datasetIndex][i], denclue.getDensityAttractors())) {
                    clusterPoints.add(dataset[datasetIndex][i]);
                }
            }

            if(clusterPoints.size()>= minPts)
            {
                double[][] cluster = new double[clusterPoints.size()][];
                clusterPoints.toArray(cluster);
                plot.points(cluster, pointLegend, Palette.COLORS[l % Palette.COLORS.length]);

                for (int i = 0; i < cluster.length; i++)
                {
                    csvWritableData.add(getCsvPoint(cluster[i], String.valueOf(l)));
                }
            }
        }

        List<double[]> attractiveList = new ArrayList<>();
        for (int i = 0; i < denclue.getDensityAttractors().length; i++)
        {
            if (null != attractivePointsMask.get(i) && attractivePointsMask.get(i))
            {
                attractiveList.add(denclue.getDensityAttractors()[i]);
                csvWritableData.add(getCsvPoint(denclue.getDensityAttractors()[i], "center"));
            }
        }

        double[][] attractiveArray = new double[attractiveList.size()][];
        attractiveList.toArray(attractiveArray);
        
        plot.points(attractiveArray, 'Q');
        pane.add(plot);

        try
        {
            CSVWriter.writeCsv(outputFile, csvWritableData);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return pane;
    }

    private boolean nearADensityAttractor(Map<Integer, Boolean> attractivePointsMask, double[] point, double[][] densityAttractors)
    {
        boolean yes  = false;
        for (int i = 0; i < densityAttractors.length; i++)
        {
            if (haversineDistance.d(point, densityAttractors[i]) <= distanceMeters)
            {
                yes = true;
                attractivePointsMask.putIfAbsent(i, true);
            }

        }
        return yes;
    }

    private List<String> getCsvPoint(double[] point, String label)
    {
        return Arrays.asList(String.valueOf(point[0]), String.valueOf(point[1]), label);
    }

    @Override
    public String toString() {
        return "DENCLUE";
    }

    public static void main(String argv[]) {
        ClusteringDemo demo = new DENCLUEDemo();
        JFrame f = new JFrame("DENCLUE");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}