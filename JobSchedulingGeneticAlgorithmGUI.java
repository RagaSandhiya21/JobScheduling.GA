import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Job {
    private String name;
    private int deadline;
    private int profit;

    public Job(String name, int deadline, int profit) {
        this.name = name;
        this.deadline = deadline;
        this.profit = profit;
    }

    public String getName() {
        return name;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getProfit() {
        return profit;
    }

    @Override
    public String toString() {
        return name + " (D: " + deadline + ", P: " + profit + ")";
    }
}

class Schedule {
    private List<Job> jobs;

    public Schedule(List<Job> jobs) {
        this.jobs = jobs;
        Collections.shuffle(this.jobs);
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public int getTotalProfit() {
        int totalProfit = 0;
        for (Job job : jobs) {
            totalProfit += job.getProfit();
        }
        return totalProfit;
    }

    @Override
    public String toString() {
        return jobs.toString();
    }
}

class Population {
    private List<Schedule> schedules;

    public Population(int populationSize, List<Job> jobs) {
        this.schedules = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            schedules.add(new Schedule(new ArrayList<>(jobs)));
        }
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public Schedule getFittest() {
        Schedule fittest = schedules.get(0);
        for (Schedule schedule : schedules) {
            if (schedule.getTotalProfit() > fittest.getTotalProfit()) {
                fittest = schedule;
            }
        }
        return fittest;
    }
}

class GeneticAlgorithm {
    private static final double mutationRate = 0.01;
    private static final int tournamentSize = 5;

    public static Population evolvePopulation(Population population) {
        Population newPopulation = new Population(population.getSchedules().size(), population.getSchedules().get(0).getJobs());

        for (int i = 0; i < population.getSchedules().size(); i++) {
            Schedule parent1 = tournamentSelection(population);
            Schedule parent2 = tournamentSelection(population);
            Schedule child = crossover(parent1, parent2);
            mutate(child);
            newPopulation.getSchedules().set(i, child);
        }

        return newPopulation;
    }

    private static Schedule tournamentSelection(Population population) {
        Population tournament = new Population(tournamentSize, population.getSchedules().get(0).getJobs());
        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = (int) (Math.random() * population.getSchedules().size());
            tournament.getSchedules().set(i, population.getSchedules().get(randomIndex));
        }
        return tournament.getFittest();
    }

    private static Schedule crossover(Schedule parent1, Schedule parent2) {
        List<Job> childJobs = new ArrayList<>(parent1.getJobs());

        for (Job job : parent2.getJobs()) {
            if (!childJobs.contains(job)) {
                childJobs.add(job);
            }
        }

        return new Schedule(childJobs);
    }

    private static void mutate(Schedule schedule) {
        for (int jobPos1 = 0; jobPos1 < schedule.getJobs().size(); jobPos1++) {
            if (Math.random() < mutationRate) {
                int jobPos2 = (int) (schedule.getJobs().size() * Math.random());

                // Swap the jobs at positions jobPos1 and jobPos2
                Collections.swap(schedule.getJobs(), jobPos1, jobPos2);
            }
        }
    }
}

public class JobSchedulingGeneticAlgorithmGUI {
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;

    private JFrame frame;
    private JTextArea outputTextArea;

    public JobSchedulingGeneticAlgorithmGUI() {
        initializeGUI();
    }
 private void initializeGUI() {
        frame = new JFrame("Job Scheduling Genetic Algorithm");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use a JPanel as the content pane to set the background image
        JPanel mainPanel = new JPanel() {
            ImageIcon background = new ImageIcon("img.jpg");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        mainPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Job Scheduling Algorithm using Genetic Algorithm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE); 
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setOpaque(false); // Make the JTextArea transparent
        outputTextArea.setForeground(Color.WHITE); // Set text color to white
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 2));

        JButton runAlgorithmButton = new JButton("Run Algorithm");
        runAlgorithmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runAlgorithm();
            }
        });

        inputPanel.add(runAlgorithmButton);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    private void runAlgorithm() {
        int numJobs = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of jobs:"));
    
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < numJobs; i++) {
            String input = JOptionPane.showInputDialog(frame, "Enter details for Job " + (i + 1) + " (name deadline profit):");
            String[] inputArray = input.split(" ");
            String name = inputArray[0];
            int deadline = Integer.parseInt(inputArray[1]);
            int profit = Integer.parseInt(inputArray[2]);
            jobs.add(new Job(name, deadline, profit));
        }
    
        int populationSize = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter population size:"));
        int generations = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of generations:"));
    
        Population population = new Population(populationSize, jobs);
    
        // Display the input in the JTextArea
        outputTextArea.setText("Given Jobs along with Name, Deadlines and Profit:\n");
        for (Job job : jobs) {
            outputTextArea.append(job.toString() + "\n");
        }
        
        for (int i = 0; i < generations; i++) {
            population = GeneticAlgorithm.evolvePopulation(population);
        }
    
        Schedule bestSchedule = population.getFittest();
    
        // Append the output to the JTextArea
        outputTextArea.append("\n\nBest Schedule: \n" + bestSchedule + "\nTotal Profit: " + bestSchedule.getTotalProfit());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JobSchedulingGeneticAlgorithmGUI();
            }
        });
    }
}


