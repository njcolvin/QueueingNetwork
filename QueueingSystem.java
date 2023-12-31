/*
 * Nicholas Colvin
 * nxc220016
 */

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class QueueingSystem {
    
    // inputs
    float lambda, pH, pL, r2D, r21, r22, mu1, mu2H, mu2L;
    // statistics
    float exTheta1H, acTheta1H, exTheta1L, acTheta1L, exTheta2H, acTheta2H, exTheta2L, acTheta2L,
          exN1H, acN1H, exN1L, acN1L, exN2H, acN2H, exN2L, acN2L, exT2H, acT2H, exT2L, acT2L;
    // state vectors
    ArrayList<Integer> simN1Hs, simN1Ls, simN2Hs, simN2Ls;

    QueueingSystem(float lambda, float pH, float r21, float r22, float mu1, float mu2H, float mu2L) {
        this.lambda = lambda;
        this.pH = pH;
        this.pL = 1.0f - pH;
        this.r2D = 1.0f - r21 - r22;
        this.r21 = r21;
        this.r22 = r22;
        this.mu1 = mu1;
        this.mu2H = mu2H;
        this.mu2L = mu2L;
        this.getMetrics();
    }

    void getMetrics() {
        // thetas
        this.exTheta1H = this.lambda * this.pH;
        this.exTheta2L = this.lambda * this.pL / this.r2D;
        this.exTheta1L = this.lambda * this.pL + this.exTheta2L * this.r21;
        this.exTheta2H = this.exTheta1H;
        // rhos
        float rho1H = this.exTheta1H / this.mu1;
        float rho1L = this.exTheta1L / this.mu1;
        float rho2H = this.exTheta2H / this.mu2H;
        float rho2L = this.exTheta2L / this.mu2L;
        // queue 1 expected waiting times in queue
        float rho1 = rho1H + rho1L;
        float eS10 = rho1 / this.mu1;
        float eTauQ1H = eS10 / (1 - rho1H);
        float eTauQ1L = (eS10 + rho1H * eTauQ1H) / (1 - rho1);
        // queue 1 expected time = waiting time in queue + service time
        float eTau1H = eTauQ1H + 1 / this.mu1;
        float eTau1L = eTauQ1L + 1 / this.mu1;

        // queue 2 expected waiting times in queue
        float eS20 = rho2H / this.mu2H + rho2L / this.mu2L;
        float eTauQ2H = eS20 / (1 - rho2H);
        float eTauQ2L = (eS20 + rho2H * eTauQ2H) / (1 - rho2H - rho2L);
        // queue 2 expected time = waiting time in queue + service time
        this.exT2H = eTauQ2H + 1 / this.mu2H;
        this.exT2L = eTauQ2L + 1 / this.mu2L;
        
        this.exN1H = this.exTheta1H * eTau1H;
        this.exN1L = this.exTheta1L * eTau1L;
        this.exN2H = this.exTheta2H * this.exT2H;
        this.exN2L = this.exTheta2L * this.exT2L;
    }

    void insertEvent(QueueEvent e, ArrayList<QueueEvent> elist) {
        int i = 0;
        while (i < elist.size() && elist.get(i).time < e.time)
            i++;
        elist.add(i, e);
    }

    void serviceNextCust1(float H1, float L1, float clock, ArrayList<QueueEvent> elist) {
        if (H1 > 0)
            insertEvent(new QueueEvent(EventType.DEPH1, clock + Generator.getExp(this.mu1)), elist);
        else if (L1 > 0)
            insertEvent(new QueueEvent(EventType.DEPL1, clock + Generator.getExp(this.mu1)), elist);
    }

    void serviceNextCust2(float H2, float L2, float clock, ArrayList<QueueEvent> elist) {
        if (H2 > 0)
            insertEvent(new QueueEvent(EventType.DEPH2, clock + Generator.getExp(this.mu2H)), elist);
        else if (L2 > 0)
            insertEvent(new QueueEvent(EventType.DEPL2, clock + Generator.getExp(this.mu2L)), elist);
    }

    void run() {
        int H1 = 0, H2 = 0, L1 = 0, L2 = 0;
        float clock = 0;
        ArrayList<QueueEvent> elist = new ArrayList<QueueEvent>();
        boolean done = false;
        this.simN1Hs = new ArrayList<Integer>();
        this.simN1Ls = new ArrayList<Integer>();
        this.simN2Hs = new ArrayList<Integer>();
        this.simN2Ls = new ArrayList<Integer>();

        int numDepH1 = 0, numDepH2 = 0, numDepL1 = 0, numDepL2 = 0, numDepSys = 0,
            numArrH1 = 0, numArrH2 = 0, numArrL1 = 0, numArrL2 = 0;
        float areaH1 = 0, areaH2 = 0, areaL1 = 0, areaL2 = 0;
        // insert first arrival
        insertEvent(new QueueEvent(EventType.ARR, Generator.getExp(this.lambda)), elist);
        while (!done) {
            // pop off the event list, update state
            QueueEvent currEvent = elist.remove(0);
            float prev = clock;
            areaH1 += H1 * (currEvent.time - clock);
            areaL1 += L1 * (currEvent.time - clock);
            areaH2 += H2 * (currEvent.time - clock);
            areaL2 += L2 * (currEvent.time - clock);
            clock = currEvent.time;
            this.simN1Hs.add(H1);
            this.simN1Ls.add(L1);
            this.simN2Hs.add(H2);
            this.simN2Ls.add(L2);
            // handle event
            switch (currEvent.type) {
                case ARR: // arrival to queue 1
                    // determine if high or low priority
                    if (Generator.getUni() < this.pH) {
                        // high priority
                        numArrH1++;
                        H1++;
                        if (L1 == 0 && H1 == 1) // arrival to empty queue1, service
                            insertEvent(new QueueEvent(EventType.DEPH1, clock + Generator.getExp(this.mu1)), elist);
                    } else {
                        // low priority
                        numArrL1++;
                        L1++;
                        if (H1 == 0 && L1 == 1) // arrival to empty queue1, service
                            insertEvent(new QueueEvent(EventType.DEPL1, clock + Generator.getExp(this.mu1)), elist);
                    }
                    // generate next arrival
                    insertEvent(new QueueEvent(EventType.ARR, clock + Generator.getExp(this.lambda)), elist);
                    break;
                case DEPH1:
                    numDepH1++;
                    H1--;
                    serviceNextCust1(H1, L1, clock, elist);
                    numArrH2++;
                    H2++;
                    if (L2 == 0 && H2 == 1) // arrival to empty queue2, service
                        insertEvent(new QueueEvent(EventType.DEPH2, clock + Generator.getExp(this.mu2H)), elist);
                    break;
                case DEPL1:
                    numDepL1++;
                    L1--;
                    serviceNextCust1(H1, L1, clock, elist);
                    numArrL2++;
                    L2++;
                    if (H2 == 0 && L2 == 1) // arrival to empty queue2, service
                        insertEvent(new QueueEvent(EventType.DEPL2, clock + Generator.getExp(this.mu2L)), elist);
                    break;
                case DEPH2:
                    numDepH2++;
                    H2--;
                    serviceNextCust2(H2, L2, clock, elist);
                    numDepSys++;
                    break;
                case DEPL2:
                    numDepL2++;
                    L2--;
                    serviceNextCust2(H2, L2, clock, elist);
                    // determine destination
                    float rvDepL2 = Generator.getUni();
                    if (rvDepL2 < this.r2D) {
                        // depart system
                        numDepSys++;
                    } else if (rvDepL2 < this.r2D + this.r21) {
                        // transfer to queue1
                        L1++;
                        if (H1 == 0 && L1 == 1) // arrival to empty queue1, service
                            insertEvent(new QueueEvent(EventType.DEPL1, clock + Generator.getExp(this.mu1)), elist);
                    } else {
                        // transfer to queue2
                        L2++;
                        if (H2 == 0 && L2 == 1) // arrival to empty queue2, service
                            insertEvent(new QueueEvent(EventType.DEPL2, clock + Generator.getExp(this.mu2L)), elist);
                    }
                    break;

            }
            if (numDepSys > 500000)
                done = true;
        }
        // E[θ] = # deps / t_end
        this.acTheta1H = numDepH1 / clock;
        this.acTheta1L = numDepL1 / clock;
        this.acTheta2H = numDepH2 / clock;
        this.acTheta2L = numDepL2 / clock;

        // E[𝜏] = area / total # arrs
        this.acT2H = areaH2 / numArrH2;
        this.acT2L = areaL2 / numArrL2;

        // E[n] = area / t_end
        this.acN1H = areaH1 / clock;
        this.acN1L = areaL1 / clock;
        this.acN2H = this.acT2H * this.acTheta2H;
        this.acN2L = this.acT2L * this.acTheta2L;

        printResults();
    }

    void printResults() {
        System.out.println("λ = " + this.lambda);
        System.out.println(" Expected E[θ_H1] = " + this.exTheta1H);
        System.out.println(" Actual E[θ_H1]   = " + this.acTheta1H);
        System.out.println(" Expected E[θ_L1] = " + this.exTheta1L);
        System.out.println(" Actual E[θ_L1]   = " + this.acTheta1L);
        System.out.println(" Expected E[θ_H2] = " + this.exTheta2H);
        System.out.println(" Actual E[θ_H2]   = " + this.acTheta2H);
        System.out.println(" Expected E[θ_L2] = " + this.exTheta2L);
        System.out.println(" Actual E[θ_L2]   = " + this.acTheta2L);
        System.out.println(" Expected E[N_H1] = " + this.exN1H);
        System.out.println(" Actual E[N_H1]   = " + this.acN1H);
        System.out.println(" Expected E[N_L1] = " + this.exN1L);
        System.out.println(" Actual E[N_L1]   = " + this.acN1L);
        System.out.println(" Expected E[N_H2] = " + this.exN2H);
        System.out.println(" Actual E[N_H2]   = " + this.acN2H);
        System.out.println(" Expected E[N_L2] = " + this.exN2L);
        System.out.println(" Actual E[N_L2]   = " + this.acN2L);
        System.out.println(" Expected E[𝜏_H2] = " + this.exT2H);
        System.out.println(" Actual E[𝜏_H2]   = " + this.acT2H);
        System.out.println(" Expected E[𝜏_L2] = " + this.exT2L);
        System.out.println(" Actual E[𝜏_L2]   = " + this.acT2L);
        System.out.println();
    }

    public static void main(String[] args) {
        if (args.length != 6)
            throw new IllegalArgumentException("usage: java QueueingSystem p_H r_21 r_22 µ_1 µ_2H µ_2L");

        float pH, r21, r22, mu1, mu2H, mu2L;
        pH = Float.parseFloat(args[0]);
        r21 = Float.parseFloat(args[1]);
        r22 = Float.parseFloat(args[2]);
        mu1 = Float.parseFloat(args[3]);
        mu2H = Float.parseFloat(args[4]);
        mu2L = Float.parseFloat(args[5]);

        // for graphs
        ArrayList<ArrayList<Integer>> states = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Float>> bigList = new ArrayList<ArrayList<Float>>();
        String[] metrics = {"Theta1H", "Theta1L", "Theta2H", "Theta2L", "N1H", "N1L", "N2H", "N2L", "T2H", "T2L"};
        for (int i = 0; i < 10; i++) {
            float lambda = i + 1;
            ArrayList<Float> lambdaStats = new ArrayList<Float>();
            QueueingSystem sys = new QueueingSystem(lambda, pH, r21, r22, mu1, mu2H, mu2L);
            sys.run();
            // add stats for graphs
            lambdaStats.add(lambda);
            for (String metric : metrics) {
                try {
                    lambdaStats.add((Float) sys.getClass().getDeclaredField("ex" + metric).get(sys));
                    lambdaStats.add((Float) sys.getClass().getDeclaredField("ac" + metric).get(sys));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // visualize simulation
            if (lambda == 10) {
                states.add(sys.simN1Hs);
                states.add(sys.simN1Ls);
                states.add(sys.simN2Hs);
                states.add(sys.simN2Ls);
            }
            bigList.add(lambdaStats);
        }
        // write states and results to files for python scripts to plot
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/states"))) {
            for (int col = 0; col < states.get(0).size(); col++) {
                for (ArrayList<Integer> row : states) {
                    int number = row.get(col);
                    writer.print(number + " ");
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/results"))) {
            for (ArrayList<Float> floats : bigList) {
                for (float number : floats) {
                    writer.print(number + " "); // Separate floats within a list with a space
                }
                writer.println(); // one list per line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}