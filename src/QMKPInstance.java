import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QMKPInstance {
    private List<Item> items;
    private List<Knapsack> knapsacks;
    private double totalWeight;

    public QMKPInstance(String filename, int numKnapsack) {
        items = new ArrayList<>();
        knapsacks = new ArrayList<>();
        dataReader(filename, numKnapsack);
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Knapsack> getKnapsacks() {
        return knapsacks;
    }

    private void dataReader(String filename, int numKnapsack) {
        System.out.println("Reading an instance ..");
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int index = 0;
            int numItem = 0;
            totalWeight = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (index == 0) {
                    // instance name
                    index++;
                } else if (index == 1) {
                    numItem = Integer.parseInt(line);
                    index++;
                } else if (index == 2) {
                    // item profit
                    String[] itemProfit = line.split("\\s+");
                    for (int i = 0; i < itemProfit.length; i++) {
                        double profit = Double.parseDouble(itemProfit[i]);
                        items.add(new Item(i,0, profit));
                    }
                    index++;
                } else if (index >= 3 && index < 2 + numItem) {
                    // quadratic profit
                    String[] itemQuadraticProfit = line.split("\\s+");
                    for (int i = 0; i < itemQuadraticProfit.length; i++) {
                        try {
                            double reader_double = Double.parseDouble(itemQuadraticProfit[i]);
                            Item item1=items.get(index-3);
                            Item item2=items.get(index-2+i);
                            item1.setQuadraticValue(item2,reader_double);
                            item2.setQuadraticValue(item1,reader_double);
                        } catch (NumberFormatException e) {
                            System.err.println("Error: Invalid input for parsing double at index " + i + ". Input string: '" + itemQuadraticProfit[i] + "'");
                            e.printStackTrace();
                        }
                    }
                    index++;
                } else if (index == numItem + 5) {
                    // item weight
                    String[] itemWeight = line.split("\\s+");
                    for (int i = 0; i < itemWeight.length; i++) {
                        double weight = Double.parseDouble(itemWeight[i]);
                        totalWeight += weight;
                        items.get(i).setWeight(weight);
                    }
                    index++;
                } else {
                    index++;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        int knapsackCapacity = (int) (0.8 * totalWeight / numKnapsack);
            for (int i = 0; i < numKnapsack; i++) {
                knapsacks.add(new Knapsack(i, knapsackCapacity));
            }
        }
    }