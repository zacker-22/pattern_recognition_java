package Assignments.week12;
import java.util.*;

class Image {
    private int[][] image;

    public Image(int[][] inputImage) {
        image = inputImage;
    }

    public void gaussian_filter() {
        int rows = image.length;
        int cols = image[0].length;
        int[][] result = new int[rows][cols];

        int[][] kernel = {
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}
        };

        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                int sum = 0;
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        sum += kernel[x + 1][y + 1] * image[i + x][j + y];
                    }
                }
                result[i][j] = sum / 16;
            }
        }

        // Update the original image with the filtered values
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                image[i][j] = result[i][j];
            }
        }
    }

    public void displayImage() {
        int rows = image.length;
        int cols = image[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(image[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Otsu's method
    private static double varianceBetween(int threshold, HashMap<Integer, Integer> counts) {
        int countLower = 0;
        int countHigher = 0;
        int sumLower = 0;
        int sumHigher = 0;
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            if (e.getKey() < threshold) {
                countLower += e.getValue();
                sumLower += e.getKey() * e.getValue();
            } else {
                countHigher += e.getValue();
                sumHigher += e.getKey() * e.getValue();
            }
        }

        if (countHigher == 0 || countLower == 0) {
            return 0.0;
        }

        double uDiff = sumLower * 1.0 / countLower - sumHigher * 1.0 / countHigher;
        return (countLower * countHigher * 1.0 / ((countLower + countHigher)^2) * 1.0) * (uDiff * uDiff) ;
    }

    public void threshold() {
        int[][] result = image.clone();

        HashMap<Integer, Integer> counts = new HashMap<>();

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                counts.put(image[i][j], counts.getOrDefault(image[i][j], 0) + 1);
            }
        }

        int threshold = -1;
        double max = -1.0;
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            double vb = varianceBetween(e.getKey(), counts);
            System.out.println("Threshold: " + e.getKey());
            System.out.println("VB: " + vb);
            if (vb > max || max == -1.0) {
                max = vb;
                threshold = e.getKey();
            }
        }
        System.out.println("Target Threshold: " + threshold);
        System.out.println("Max vb: " + max);

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                if (image[i][j] < threshold) {
                    image[i][j] = 0;
                } else {
                    image[i][j] = 1;
                }
            }
        }

        
    }

    public int[][] getConnectedComponents() {
        int[][] result = image.clone();

        int rows = image.length;
        int cols = image[0].length;

        int[][] visited = new int[rows][cols];

        int[][] directions = {
            {0, 1},
            {1, 0},
            {0, -1},
            {-1, 0}
        };


        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0 ; j < cols; j++) {
                if (visited[i][j] == 0 && image[i][j] == 1) {
                    count++;
                    Queue<int[]> q = new LinkedList<>();
                    q.add(new int[]{i, j});
                    while (!q.isEmpty()) {
                        int[] curr = q.poll();
                        int x = curr[0];
                        int y = curr[1];
                        visited[x][y] = 1;
                        result[x][y] = count;
                        for (int[] dir : directions) {
                            int newX = x + dir[0];
                            int newY = y + dir[1];
                            if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && visited[newX][newY] == 0 && image[newX][newY] == 1) {
                                q.add(new int[]{newX, newY});
                            }
                        }
                    }
                }
            }
        }

        return result;

    }

    
}


public class TestImage {

    private int[][] getAreaAndPerimeterOfConnectedComponents(int[][] connectedComponents){
        int rows = connectedComponents.length;
        int cols = connectedComponents[0].length;

        Map<Integer, Integer> area = new HashMap<>();
        Map<Integer, Integer> perimeter = new HashMap<>();

        int[][] directions = {
            {0, 1},
            {1, 0},
            {0, -1},
            {-1, 0}
        };

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(connectedComponents[i][j] != 0){
                    int count = 0;
                    int count2 = 0;
                    for(int[] dir : directions){
                        int newX = i + dir[0];
                        int newY = j + dir[1];
                        if(newX >= 0 && newX < rows && newY >= 0 && newY < cols && connectedComponents[newX][newY] == 0){
                            count++;
                        }
                        if(newX >= 0 && newX < rows && newY >= 0 && newY < cols && connectedComponents[newX][newY] != 0){
                            count2++;
                        }
                    }
                    area.put(connectedComponents[i][j], count2);
                    perimeter.put(connectedComponents[i][j], count);
                }
            }
        }

        int[][] result = new int[area.size()][2];
        int index = 0;
        for(Map.Entry<Integer, Integer> e : area.entrySet()){
            result[index][0] = e.getKey();
            result[index][1] = e.getValue();
            index++;
        }

        return result;


    }

    private bool isSquare(int area, int perimeter){
        return  4*Math.PI*area/(perimeter*perimeter) > 0.8 ? true : false;
    }

    private bool isCircle(int area, int perimeter){
        return  4*Math.PI*area/(perimeter*perimeter) < 0.8 ? true : false;
    }
    public static void main(String[] args) {

        // Step A
        
        int[][] inputImage = {
            {1, 3, 5, 7, 9, 3, 4, 4, 5, 6},
            {1, 20, 25, 24, 3, 5, 6, 4, 2, 4},
            {1, 22, 35, 24, 3, 5, 6, 4, 5, 7},
            {1, 20, 28, 34, 2, 5, 6, 4, 8, 9},
            {1, 3, 5, 7, 9, 3, 4, 4, 5, 6},
            {1, 3, 5, 7, 9, 3, 67, 4, 5, 6},
            {1, 3, 5, 7, 9, 78, 54, 94, 5, 6},
            {1, 3, 5, 7, 9, 99, 98, 54, 5, 6},
            {1, 3, 5, 7, 9, 3, 64, 4, 5, 6},
            {1, 3, 5, 7, 9, 3, 4, 4, 5, 6}
        };

        
        Image image = new Image(inputImage);

        System.out.println("Image after Step A");
        image.displayImage();

        // image.gaussian_filter();
        image.displayImage();
        image.threshold();
        System.out.println("Binary Image");
        image.displayImage();

        int[][] connectedComponents = image.getConnectedComponents();
        System.out.println("Connected Components");
        for (int i = 0; i < connectedComponents.length; i++) {
            for (int j = 0; j < connectedComponents[0].length; j++) {
                System.out.print(connectedComponents[i][j] + " ");
            }
            System.out.println();
        }


        int[][] areaAndPerimeter = new TestImage().getAreaAndPerimeterOfConnectedComponents(connectedComponents);
        System.out.println("Area and Perimeter");
        for (int i = 0; i < areaAndPerimeter.length; i++) {
            for (int j = 0; j < areaAndPerimeter[0].length; j++) {
                System.out.print(areaAndPerimeter[i][j] + " ");
            }
            System.out.println();
        }

        for(int i = 0; i < areaAndPerimeter.length; i++){
            if(isSquare(areaAndPerimeter[i][0], areaAndPerimeter[i][1])){
                System.out.println("Square");
            }
            else if(isCircle(areaAndPerimeter[i][0], areaAndPerimeter[i][1])){
                System.out.println("Circle");
            }
            else{
                System.out.println("Unknown");
            }
        }
    }
}