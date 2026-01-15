import java.util.Scanner;

public class MovieManagementSystem {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";

    static int totalHalls;
    static int seatsPerHall;
    static String[][] halls;
    static String[] movieNames;
    static String[] movieTypes;
    static int[] movieDurations;
    static boolean[] hallOccupied;

    static String lastBookingInfo = "No recent booking found.";
    static int lastHallId = -1;
    static boolean hasBooked = false;

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        setupCinema();
        showMenu();
    }

    static int readInt() {
        while (true) {
            try {
                String input = sc.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("!!! Invalid Input. Please enter a number: /t");
            }
        }
    }

    static void setupCinema() {
        System.out.println("-------- Setting up Cinema ---------");
        System.out.print("-> Enter number of Hall in Cinema: ");
        totalHalls = readInt();
        System.out.print("-> Enter number of seat in each Hall: ");
        seatsPerHall = readInt();

        halls = new String[totalHalls][seatsPerHall];
        movieNames = new String[totalHalls];
        movieTypes = new String[totalHalls];
        movieDurations = new int[totalHalls];
        hallOccupied = new boolean[totalHalls];

        for (int i = 0; i < totalHalls; i++) {
            for (int j = 0; j < seatsPerHall; j++) {
                halls[i][j] = "(+)";
            }
        }
    }

    static void showMenu() {
        while (true) {
            System.out.println("\n----------------- Movie Management System -----------------");
            System.out.println("1- Insert Movie");
            System.out.println("2- Check & Booking Movie");
            System.out.println("3- Check Ticket");
            System.out.println("4- Reset Hall");
            System.out.println("5- Exit");
            System.out.println("-----------------------------------------------------------");
            System.out.print("-> Choose option (1-5): ");
            int choice = readInt();

            switch (choice) {
                case 1 -> insertMovie();
                case 2 -> bookingProcess();
                case 3 -> checkTicket();
                case 4 -> resetHall();
                case 5 -> {
                    System.out.println("-> Good bye!");
                    System.exit(0);
                }
                default -> System.out.println("!!! Reject: Please choose only options 1 to 5.");
            }
        }
    }

    static void insertMovie() {
        System.out.println("\n--------- Insert information of movie ---------");
        int assignedHall = -1;
        for(int i=0; i<totalHalls; i++) {
            if(!hallOccupied[i]) {
                assignedHall = i;
                break;
            }
        }
        // this is just for the testing and i have to do it by myself
        if(assignedHall == -1) {
            System.out.println("!!! Reject: All halls are currently full!");
            return;
        }
        System.out.print("-> Enter Movie Name: ");
        movieNames[assignedHall] = sc.nextLine();
        System.out.print("-> Enter Movie Type: ");
        movieTypes[assignedHall] = sc.nextLine();
        System.out.print("-> Enter Duration (min): ");
        movieDurations[assignedHall] = readInt();
        hallOccupied[assignedHall] = true;
        System.out.println("\nMovie '" + movieNames[assignedHall] + "' will show in hall #" + (assignedHall + 1));
    }

    static void bookingProcess() {
        System.out.println("\n--------- Display All Movie ---------");
        boolean anyMovie = false;
        System.out.printf("%-5s %-15s %-10s %-10s %-5s %-10s %-10s\n", "ID", "Movie", "Type", "Duration", "Hall", "Seat", "Available");
        for (int i = 0; i < totalHalls; i++) {
            if (hallOccupied[i]) {
                anyMovie = true;
                int avail = 0;
                for(String s : halls[i]) if(s.equals("(+)")) avail++;
                System.out.printf("%-5d %-15s %-10s %-10d %-5d %-10d %-10d\n",
                        (i+1), movieNames[i], movieTypes[i], movieDurations[i], (i+1), seatsPerHall, avail);
            }
        }
        if(!anyMovie) {
            System.out.println("!!! Reject: No movies are currently showing.");
            return;
        }
        System.out.print("\n-> Enter movie's Id to detail: ");
        int id = readInt() - 1;
        if(id < 0 || id >= totalHalls || !hallOccupied[id]) {
            System.out.println("!!! Reject: This Hall ID does not exist.");
            return;
        }

        // Show Seat Grid
        displayHallMap(id);

        System.out.print("\n-> Choose seat that you want to booking(e.g: 1,2,3,4): ");
        String selected = sc.nextLine();
        System.out.print("=> Do you want to book Seat number " + selected + "? (Y/N): ");
        if(sc.nextLine().equalsIgnoreCase("y")) {
            try {
                String[] seatsArray = selected.split(",");
                for(String s : seatsArray) {
                    int seatIdx = Integer.parseInt(s.trim()) - 1;
                    if(seatIdx < 0 || seatIdx >= seatsPerHall) {
                        System.out.println("!!! Reject: Seat doesn't exist.");
                        return;
                    }
                    if(halls[id][seatIdx].equals("(-)")) {
                        System.out.println("!!! Reject: Seat " + (seatIdx + 1) + " is already booked.");
                        return;
                    }
                    halls[id][seatIdx] = "(-)";
                }

                // SAVE TICKET INFO
                hasBooked = true;
                lastHallId = id; // Save the hall ID so Option 3 can display the map
                lastBookingInfo = RED + "===================================\n" + RESET +
                        GREEN + "Your ticket has been booked!\n" + RESET +
                        RED + "===================================\n" + RESET +
                        "Hall: #" + (id + 1) + "\n" +
                        "Movie: " + movieNames[id] + "\n" +
                        "Seat Booked: " + RED + selected + RESET + "\n" +
                        "-----------------------------------";

                System.out.println("\nSeat number " + selected + " was booked successfully!");
            } catch (Exception e) {
                System.out.println("!!! Reject: Invalid seat format.");
            }
        }
    }

    static void displayHallMap(int id) {
        System.out.println("\n------------------ Screen Hall #" + (id + 1) + " ------------------");
        for (int i = 0; i < seatsPerHall; i++) {
            String seatStatus = halls[id][i];
            if (seatStatus.equals("(+)")) {
                System.out.print(GREEN + seatStatus + " " + (i + 1) + RESET + "\t");
            } else {
                System.out.print(RED + seatStatus + " " + (i + 1) + RESET + "\t");
            }
            if ((i + 1) % 5 == 0) System.out.println();
        }
    }

    static void checkTicket() {
        if (!hasBooked) {
            System.out.println("\n!!! No booking history found.");
        } else {
            // First, show the current status of the hall visually
            displayHallMap(lastHallId);
            // Then, show the text ticket details
            System.out.println("\n" + lastBookingInfo);
        }
    }

    static void resetHall() {
        System.out.print("=> All Hall was reset with all seats available? (y/n): ");
        if(sc.nextLine().equalsIgnoreCase("y")) {
            for (int i = 0; i < totalHalls; i++) {
                hallOccupied[i] = false;
                for (int j = 0; j < seatsPerHall; j++) {
                    halls[i][j] = "(+)";
                }
            }
            hasBooked = false;
            lastHallId = -1;
            System.out.println("All Hall was reset successfully");
        }
    }
}