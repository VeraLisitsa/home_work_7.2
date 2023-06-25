import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        int countRoutes = 1000;
        List<Thread> threads = new ArrayList<>();
        AtomicBoolean waitThread = new AtomicBoolean(false);

        Thread freq = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    waitThread.set(true);
                    sizeToFreq.notifyAll();
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    LinkedList<Map.Entry<Integer, Integer>> list = new LinkedList<>(sizeToFreq.entrySet());
                    list.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()));
                    Map.Entry<Integer, Integer> max = list.getLast();
                    System.out.printf("Текущий лидер среди частот -  %d (встретилось %d раз)\n", max.getKey(), max.getValue());
                }
            }
        });
        freq.start();

        for (int i = 0; i < countRoutes; i++) {
            Thread thread = new Thread(() ->
            {
                String str = generateRoute("RLRFR", 100);
                int countR = 0;
                for (int j = 0; j < str.length(); j++) {
                    if (str.charAt(j) == 'R') {
                        countR++;
                    }
                }
                synchronized (sizeToFreq) {
                    while (waitThread.get() == false) {
                        try {
                            sizeToFreq.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (sizeToFreq.containsKey(countR)) {
                        int countFreqR = sizeToFreq.get(countR) + 1;
                        sizeToFreq.put(countR, countFreqR);
                    } else {
                        sizeToFreq.put(countR, 1);
                    }
                    waitThread.set(false);
                    sizeToFreq.notify();
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        freq.interrupt();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

}