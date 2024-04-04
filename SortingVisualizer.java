import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;

public class SortingVisualizer extends JFrame {
    private int[] array;
    private int barWidth = 30;
    private int delay = 80;
    private Color[] barColors;
    private static final Color ORIGINAL_COLOR = new Color(255, 215, 0);   //Yellow

    private JPanel arrayPanel;
    private JButton startButton;
    private JComboBox<String> algorithmComboBox;
    private JButton resetButton;
    private JLabel comparisonCountLabel;
    private JTextField arraySizeField;
    private JTextField arrayElementsField;
    private JTextField barWidthField;
    private JTextField delayField;

    private int selectedComparisonIndex = -1;

    public SortingVisualizer() {
        setTitle("Sorting Algorithm Visualizer");
        setSize(1980, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        JLabel arraySizeLabel = new JLabel("Array Size:");
        arraySizeField = new JTextField("10");
        JLabel arrayElementsLabel = new JLabel("Array Elements (comma-separated):");
        arrayElementsField = new JTextField("1,2,3,...");
        JLabel barWidthLabel = new JLabel("Bar Width:");
        barWidthField = new JTextField("40");
        JLabel delayLabel = new JLabel("Delay (ms):");
        delayField = new JTextField("80");

        inputPanel.add(arraySizeLabel);
        inputPanel.add(arraySizeField);
        inputPanel.add(arrayElementsLabel);
        inputPanel.add(arrayElementsField);
        inputPanel.add(barWidthLabel);
        inputPanel.add(barWidthField);
        inputPanel.add(delayLabel);
        inputPanel.add(delayField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        startButton = new JButton("Start");
        startButton.setBackground(new Color(52, 152, 219));   //Blue
        startButton.setForeground(Color.WHITE);
        algorithmComboBox = new JComboBox<>(new String[] { "Bubble Sort", "Selection Sort", "Heap Sort", "Insertion Sort", "Merge Sort", "Quick Sort" });
        resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(46, 204, 113));   //Green
        resetButton.setForeground(Color.WHITE);
        comparisonCountLabel = new JLabel("Comparisons: 0");
        comparisonCountLabel.setFont(new Font("Arial", Font.BOLD, 18));

        buttonPanel.add(startButton);
        buttonPanel.add(algorithmComboBox);
        buttonPanel.add(resetButton);
        buttonPanel.add(comparisonCountLabel);

        controlPanel.add(inputPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.NORTH);

        arrayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (array != null) {
                    for (int i = 0; i < array.length; i++) {
                        int x = i * barWidth;
                        int height = array[i];
                        int y = (getHeight() - height);

                        g.setColor(Color.BLACK);
                        g.drawRect(x, y, barWidth, height);

                        if (i == selectedComparisonIndex) {
                            g.setColor(Color.RED);
                            g.fillRect(x + 1, y + 1, barWidth - 1, height - 1);
                        } else {
                            g.setColor(ORIGINAL_COLOR);
                            g.fillRect(x + 1, y + 1, barWidth - 1, height - 1);
                        }

                        g.setColor(Color.BLACK);
                        g.drawString(Integer.toString(array[i]), x + barWidth / 2 - 5, y - 5);
                    }
                }
            }
        };
        arrayPanel.setPreferredSize(new Dimension(900, 600));
        add(arrayPanel, BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateRandomArray();
                startSorting();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean wasSorting = isSorting;  // Store the previous sorting state
                isSorting = false;
                Thread.currentThread().interrupt();
                selectedComparisonIndex = -1;
                generateRandomArray();
                comparisonCountLabel.setText("Comparisons: 0");
                repaint();
                if (wasSorting) {
                    JOptionPane.showMessageDialog(SortingVisualizer.this, "Sorting Is Interrupted", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
                
            }
        });
    }

    private void startSorting() {
        isSorting = true;
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        switch (selectedAlgorithm) {
            case "Bubble Sort":
                if (!isSorted(array)) {   
                    BubbleSorting();
                } else {
                    JOptionPane.showMessageDialog(this, "Data Aldready Sorted", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "Selection Sort":
                if (!isSorted(array)) {
                    SelectionSorting();
                } else {
                    JOptionPane.showMessageDialog(this, "Data Aldready Sorted", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "Heap Sort":
                if (!isSorted(array)) {
                    HeapSorting();
                } else {
                    JOptionPane.showMessageDialog(this, "Data Aldready Sorted", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "Insertion Sort":
                if (!isSorted(array)) {
                    InsertionSorting();
                } else {
                    JOptionPane.showMessageDialog(this, "Data Aldready Sorted", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "Merge Sort":
                if (!isSorted(array)) {
                    startMergeSort();
                } else {
                    JOptionPane.showMessageDialog(this, "Data Aldready Sorted", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "Quick Sort":
                if (!isSorted(array)) {
                    startQuickSort(0, array.length - 1);
                } else {
                    JOptionPane.showMessageDialog(this, "Data Aldready Sorted", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                break;
        }
    }

    private boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }

    private void sortingCompleted() {
        isSorting = false;
        selectedComparisonIndex = -1;
        JOptionPane.showMessageDialog(this, "The Sorting Is Completed", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isSorting = false;

    private void generateRandomArray() {
        int arraySize = Integer.parseInt(arraySizeField.getText());
        array = new int[arraySize];
        barColors = new Color[arraySize];
        String[] elements = arrayElementsField.getText().split(",");
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(elements[i].trim());
            barColors[i] = ORIGINAL_COLOR;
        }
        barWidth = Integer.parseInt(barWidthField.getText());
        delay = Integer.parseInt(delayField.getText());
        repaint();
    }

    private void updateComparisonCount(int count) {
        SwingUtilities.invokeLater(() -> {
            comparisonCountLabel.setText("Comparisons: " + count);
        });
    }

    private void BubbleSorting() {
        new Thread(() -> {
            int comparisonsCount = 0;
            for (int i = 0; i < array.length - 1 && isSorting; i++) {
                for (int j = 0; j < array.length - i - 1; j++) {
                    comparisonsCount++;
                    if (array[j] > array[j + 1]) {
                        swap(j, j + 1);
                        selectedComparisonIndex = j + 1;   
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    repaint();
                }
            }
            updateComparisonCount(comparisonsCount);
            if(isSorting)
            {
                sortingCompleted();
            }
        }).start();
    }

    private void SelectionSorting() {
        new Thread(() -> {
            int comparisonsCount = 0;
            for (int i = 0; i < array.length - 1 && isSorting; i++) {
                int minIndex = i;
                for (int j = i + 1; j < array.length; j++) {
                    comparisonsCount++;
                    if (array[j] < array[minIndex]) {
                        minIndex = j;
                    }
                }
                swap(i, minIndex);
                selectedComparisonIndex = i;   
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                repaint();
            }
            updateComparisonCount(comparisonsCount);
            if(isSorting)
            {
                sortingCompleted();
            }
        }).start();
    }

    private void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private void InsertionSorting() {
        new Thread(() -> {
            int comparisonsCount = 0;
            for (int i = 1; i < array.length && isSorting; i++) {
                int key = array[i];
                int j = i - 1;

                while (j >= 0 && array[j] > key) {
                    comparisonsCount++;
                    updateComparisonCount(comparisonsCount);

                    array[j + 1] = array[j];
                    j--;
                }
                array[j + 1] = key;
                selectedComparisonIndex = j + 1;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                repaint();
            }
            if(isSorting)
            {
                sortingCompleted();
            }
        }).start();
    }

    private void HeapSorting() {
        new Thread(() -> {
            AtomicInteger comparisonsCount = new AtomicInteger(0); 
            int n = array.length;
    
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(n, i, comparisonsCount);
            }
    
            for (int i = n - 1; i > 0 && isSorting; i--) {
                swap(0, i);
                heapify(i, 0, comparisonsCount);
                selectedComparisonIndex = 0;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                repaint();
            }
            updateComparisonCount(comparisonsCount.get());
            if(isSorting)
            {
                sortingCompleted();
            }
        }).start();
    }

    private void heapify(int n, int i, AtomicInteger comparisonsCount) {
    int largest = i;
    int left = 2 * i + 1;
    int right = 2 * i + 2;

    if (left < n) {
        comparisonsCount.incrementAndGet();
        if (array[left] > array[largest]) {
            largest = left;
        }
    }

    if (right < n) {
        comparisonsCount.incrementAndGet();
        if (array[right] > array[largest]) {
            largest = right;
        }
    }

    if (largest != i) {
        swap(i, largest);
        heapify(n, largest, comparisonsCount);
        selectedComparisonIndex = largest;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        repaint();
    }
}

private void startQuickSort(int low, int high) {
    new Thread(() -> {
        AtomicInteger comparisonsCount = new AtomicInteger(0); 
        quickSort(array, low, high, comparisonsCount);
        updateComparisonCount(comparisonsCount.get());
        repaint();
        if(isSorting)
        {
            sortingCompleted();
        }
    }).start();
}

    private void quickSort(int[] arr, int low, int high, AtomicInteger comparisonsCount) {
        if (low < high && isSorting) {
            int pi = partition(arr, low, high, comparisonsCount);
            quickSort(arr, low, pi - 1, comparisonsCount);
            quickSort(arr, pi + 1, high, comparisonsCount);
        }
    }

    private int partition(int[] arr, int low, int high, AtomicInteger comparisonsCount) {
        int pivot = arr[high];
        int i = (low - 1);
    
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(i, j);
            }
            comparisonsCount.incrementAndGet();
            selectedComparisonIndex = j;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            repaint();
        }
        swap(i + 1, high);
        return i + 1;
    }

    private void startMergeSort() {
        new Thread(() -> {
            AtomicInteger comparisonsCount = new AtomicInteger(0); 
            mergeSort(array, 0, array.length - 1, comparisonsCount);
            updateComparisonCount(comparisonsCount.get()); 
            repaint();
            if(isSorting)
            {
                sortingCompleted();
            }
        }).start();
    }

    private void mergeSort(int[] arr, int l, int r, AtomicInteger comparisonsCount) {
        if (!isSorting) return;
        if (l < r) {
            int m = (l + r) / 2;
            mergeSort(arr, l, m, comparisonsCount);
            mergeSort(arr, m + 1, r, comparisonsCount);
            merge(arr, l, m, r, comparisonsCount);
        }
    }

    private void merge(int[] arr, int l, int m, int r, AtomicInteger comparisonsCount) {
        int n1 = m - l + 1;
        int n2 = r - m;
    
        int[] L = new int[n1];
        int[] R = new int[n2];
    
        for (int i = 0; i < n1; ++i)
            L[i] = arr[l + i];
        for (int j = 0; j < n2; ++j)
            R[j] = arr[m + 1 + j];
    
        int i = 0, j = 0;
    
        int k = l;
    
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            comparisonsCount.incrementAndGet();
            k++;
            selectedComparisonIndex = k;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            repaint();
        }
    
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
            selectedComparisonIndex = k;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            repaint();
        }
    
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
            selectedComparisonIndex = k;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            repaint();
        }
        selectedComparisonIndex = -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SortingVisualizer::new);
    }
}
