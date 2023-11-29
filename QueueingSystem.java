import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class QueueingSystem {
    
    // inputs
    float lambda, pH, pL, r2D, r21, r22, mu1, mu2H, mu2L;
    // statistics
    float exY1H, acY1H, exY1L, acY1L, exY2H, acY2H, exY2L, acY2L, exN1H, acN1H,
          exN1L, acN1L, exN2H, acN2H, exN2L, acN2L, exT2H, acT2H, exT2L, acT2L;

    QueueingSystem(float lambda, float pH, float pL, float r2D, float r21,
                   float r22, float mu1, float mu2H, float mu2L) {
        this.lambda = lambda;
        this.pH = pH;
        this.pL = pL;
        this.r2D = r2D;
        this.r21 = r21;
        this.r22 = r22;
        this.mu1 = mu1;
        this.mu2H = mu2H;
        this.mu2L = mu2L;
        this.computeStateProbs();
        this.computeMetrics();
    }

    // TODO: compute state probabilities
    void computeStateProbs() {
        ArrayList<Float> stateProbs;
        ArrayList<Float> coefficients = new ArrayList<Float>();
        // float numerator = this.getLambda() + this.gamma, denominator = this.mu;
        // // both machines working, p(1) = p(0) * (λ + γ) / µ
        // coefficients.add(numerator / denominator); 
        // numerator *= this.getLambda() + this.gamma;
        // // multiply µ by number of workers
        // denominator *= this.m > 1 ? 2 * this.mu : this.mu;
        // // both machines working, p(2) = p(0) * (λ + γ)^2 / if(m > 1, 2µ^2, µ^2)
        // coefficients.add(numerator / denominator);
        // for (int i = 2; i < this.K; i++) {
        //     // only machine 2 working
        //     numerator *= this.getLambda();
        //     denominator *= this.m > i ? i * this.mu : this.m * this.mu;
        //     // for i >= 2, p(i) = p(0) * coef(p(i-1)) * λ / if(m > i, iµ, mµ)
        //     coefficients.add(numerator / denominator); 
        // }
        // // compute p(0) = 1 / (1 + (λ + γ) / µ + (λ + γ)^2 / if(m > 1, 2µ^2, µ^2) + ...)
        // float sum = 1;
        // for (float f : coefficients)
        //     sum += f;
        // this.stateProbs.add(1 / sum);
        // // compute p(1)..p(K)
        // for (int i = 0; i < coefficients.size(); i++)
        //     this.stateProbs.add(coefficients.get(i) * this.stateProbs.get(0));
    }

    // TODO: compute metrics
    void computeMetrics() {
        // // E[n] = i * p(i) for i = 0..K
        // this.expectedNumCust = 0;
        // for (int i = 0; i <= this.K; i++)
        //     expectedNumCust += i * this.stateProbs.get(i);
        // // E[𝜏] = E[n] / λ_avg (Little's Law)
        // // λ_avg = ((λ + γ)p(0) + (λ + γ)p(1) + λp(2) + ... + λp(k))
        // float lambdaAvg = 0;
        // lambdaAvg += (this.getLambda() + this.gamma) * this.stateProbs.get(0);
        // lambdaAvg += (this.getLambda() + this.gamma) * this.stateProbs.get(1);
        // for (int i = 2; i <= this.K; i++)
        //     lambdaAvg += this.getLambda() * this.stateProbs.get(i);
        // this.expectedTimeCust = this.expectedNumCust / lambdaAvg;
        // // P(block) = λp(k) / λ_avg
        // this.expectedProbBlock = this.getLambda() * this.stateProbs.get(this.K) / lambdaAvg;
        // // Utilization = 1/2 * p(1) + p(2) + p(3) + p(4)
        // this.expectedUtil = this.stateProbs.get(1) / 2;
        // for (int i = 2; i <= this.K; i++)
        //     this.expectedUtil += this.stateProbs.get(i);
    }

    void insertEvent(Event e, ArrayList<Event> elist) {
        int i = 0;
        while (i < elist.size() && elist.get(i).time < e.time)
            i++;
        elist.add(i, e);
    }

    void serviceNextCust1(float H1, float L1, float clock, ArrayList<Event> elist) {
        if (H1 > 0)
            insertEvent(new Event(EventType.DEPH1, clock + Generator.getExp(this.mu1)), elist);
        else if (L1 > 0)
            insertEvent(new Event(EventType.DEPL1, clock + Generator.getExp(this.mu1)), elist);
    }

    void serviceNextCust2(float H2, float L2, float clock, ArrayList<Event> elist) {
        if (H2 > 0)
            insertEvent(new Event(EventType.DEPH2, clock + Generator.getExp(this.mu2H)), elist);
        else if (L2 > 0)
            insertEvent(new Event(EventType.DEPL2, clock + Generator.getExp(this.mu2L)), elist);
    }

    void run() {
        int H1 = 0, H2 = 0, L1 = 0, L2 = 0;
        float clock = 0, rvDepL2;
        ArrayList<Event> elist = new ArrayList<Event>();
        boolean done = false;

        int numDepH1 = 0, numDepH2 = 0, numDepL1 = 0, numDepL2 = 0, numDepSys = 0,
            numArrH1 = 0, numArrH2 = 0, numArrL1 = 0, numArrL2 = 0;
        float areaH1 = 0, areaH2 = 0, areaL1 = 0, areaL2 = 0;
        // insert first arrival
        insertEvent(new Event(EventType.ARR, Generator.getExp(this.lambda)), elist);
        while (!done) {
            // pop off the event list, update state
            Event currEvent = elist.remove(0);
            areaH1 += H1 * (currEvent.time - clock);
            areaH2 += H2 * (currEvent.time - clock);
            areaL1 += L1 * (currEvent.time - clock);
            areaH1 += H1 * (currEvent.time - clock);
            clock = currEvent.time;
            // handle event
            switch (currEvent.type) {
                case ARR: // arrival to queue 1
                    // determine if high or low priority
                    if (Generator.getUni() < this.pH) {
                        // high priority
                        numArrH1++;
                        H1++;
                        if (L1 == 0 && H1 == 1) // arrival to empty queue1, service
                            insertEvent(new Event(EventType.DEPH1, clock + Generator.getExp(this.mu1)), elist);
                    } else {
                        // low priority
                        numArrL1++;
                        L1++;
                        if (H1 == 0 && L1 == 1) // arrival to empty queue1, service
                            insertEvent(new Event(EventType.DEPL1, clock + Generator.getExp(this.mu1)), elist);
                    }
                    // generate next arrival
                    insertEvent(new Event(EventType.ARR, clock + Generator.getExp(this.lambda)), elist);
                    break;
                case DEPH1:
                    numDepH1++;
                    H1--;
                    serviceNextCust1(H1, L1, clock, elist);
                    numArrH2++;
                    H2++;
                    if (L2 == 0 && H2 == 1) // arrival to empty queue2, service
                        insertEvent(new Event(EventType.DEPH2, clock + Generator.getExp(this.mu2H)), elist);
                    break;
                case DEPL1:
                    numDepL1++;
                    L1--;
                    serviceNextCust1(H1, L1, clock, elist);
                    numArrL2++;
                    L2++;
                    if (H2 == 0 && L2 == 1) // arrival to empty queue2, service
                        insertEvent(new Event(EventType.DEPL2, clock + Generator.getExp(this.mu2L)), elist);
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
                    rvDepL2 = Generator.getUni();
                    if (rvDepL2 < this.r2D) {
                        // depart system
                        numDepSys++;
                    } else if (rvDepL2 < this.r2D + this.r21) {
                        // transfer to queue1
                        numArrL1++;
                        L1++;
                        if (H1 == 0 && L1 == 1) // arrival to empty queue1, service
                            insertEvent(new Event(EventType.DEPL1, clock + Generator.getExp(this.mu1)), elist);
                    } else {
                        // transfer to queue2
                        numArrL2++;
                        L2++;
                        if (H2 == 0 && L2 == 1) // arrival to empty queue2, service
                            insertEvent(new Event(EventType.DEPL2, clock + Generator.getExp(this.mu2L)), elist);
                    }
                    break;

            }
            if (numDepSys > 500000)
                done = true;
        }

        System.out.println(numArrH1);
        System.out.println(numArrL1);
        System.out.println(numArrH2);
        System.out.println(numArrL2);
        System.out.println(numDepH1);
        System.out.println(numDepL1);
        System.out.println(numDepH2);
        System.out.println(numDepL2);

        // TODO: print results
        // System.out.println("ρ = " + this.rho);
        // System.out.println(" State Probabilities");
        // for (int i = 0; i <= this.K; i++)
        //     System.out.println("  p(" + i + ") = " + this.stateProbs.get(i));

        // // E[n] = area / t_end
        // System.out.println(" Expected E[n] = " + this.expectedNumCust);
        // this.actualNumCust = area / this.clock;
        // System.out.println(" Actual E[n]   = " + this.actualNumCust);

        // // E[𝜏] = area / total # arrs
        // System.out.println(" Expected E[𝜏] = " + this.expectedTimeCust);
        // this.actualTimeCust = area / numArr;
        // System.out.println(" Actual E[𝜏]   = " + this.actualTimeCust);
        
        // // P(block) = total # blocks / total # arrs
        // System.out.println(" Expected P(block) = " + this.expectedProbBlock);
        // this.actualProbBlock = (float) numBlock / numArr;
        // System.out.println(" Actual P(block)   = " + this.actualProbBlock);

        // // Utilization computed similarly to area
        // System.out.println(" Expected Utilization = " + this.expectedUtil);
        // this.actualUtil /= this.clock;
        // System.out.println(" Actual Utilization   = " + this.actualUtil);
        // System.out.println();
    }

    public static void main(String[] args) {
        if (args.length != 6)
            throw new IllegalArgumentException("usage: java QueueingSystem p_H r_21 r_22 µ_1 µ_2H µ_2L");

        float pH, pL, r2D, r21, r22, mu1, mu2H, mu2L;
        pH = Float.parseFloat(args[0]);
        pL = 1.0f - pH;
        r21 = Float.parseFloat(args[1]);
        r22 = Float.parseFloat(args[2]);
        r2D = 1.0f - r21 - r22;
        mu1 = Float.parseFloat(args[3]);
        mu2H = Float.parseFloat(args[4]);
        mu2L = Float.parseFloat(args[5]);

        QueueingSystem sys;
        float lambda;
        // for graphs
        ArrayList<Float> lambdas = new ArrayList<Float>();
        ArrayList<Float> exY1Hs = new ArrayList<Float>();
        ArrayList<Float> acY1Hs = new ArrayList<Float>();
        ArrayList<Float> exY1Ls = new ArrayList<Float>();
        ArrayList<Float> acY1Ls = new ArrayList<Float>();
        ArrayList<Float> exY2Hs = new ArrayList<Float>();
        ArrayList<Float> acY2Hs = new ArrayList<Float>();
        ArrayList<Float> exY2Ls = new ArrayList<Float>();
        ArrayList<Float> acY2Ls = new ArrayList<Float>();
        ArrayList<Float> exN1Hs = new ArrayList<Float>();
        ArrayList<Float> acN1Hs = new ArrayList<Float>();
        ArrayList<Float> exN1Ls = new ArrayList<Float>();
        ArrayList<Float> acN1Ls = new ArrayList<Float>();
        ArrayList<Float> exN2Hs = new ArrayList<Float>();
        ArrayList<Float> acN2Hs = new ArrayList<Float>();
        ArrayList<Float> exN2Ls = new ArrayList<Float>();
        ArrayList<Float> acN2Ls = new ArrayList<Float>();
        ArrayList<Float> exT2Hs = new ArrayList<Float>();
        ArrayList<Float> acT2Hs = new ArrayList<Float>();
        ArrayList<Float> exT2Ls = new ArrayList<Float>();
        ArrayList<Float> acT2Ls = new ArrayList<Float>();
        
        for (int i = 0; i < 10; i++) {
            lambda = i + 1;
            lambdas.add(lambda);
            sys = new QueueingSystem(lambda, pH, pL, r2D, r21, r22, mu1, mu2H, mu2L);
            sys.run();
            System.out.println();
            // TODO: append stats to graphs
        }
        ArrayList<ArrayList<Float>> bigList = new ArrayList<ArrayList<Float>>();
        bigList.add(lambdas);
        bigList.add(exY1Hs);
        bigList.add(acY1Hs);
        bigList.add(exY1Ls);
        bigList.add(acY1Ls);
        bigList.add(exY2Hs);
        bigList.add(acY2Hs);
        bigList.add(exY2Ls);
        bigList.add(acY2Ls);
        bigList.add(exN1Hs);
        bigList.add(acN1Hs);
        bigList.add(exN1Ls);
        bigList.add(acN1Ls);
        bigList.add(acY1Ls);
        bigList.add(exN2Hs);
        bigList.add(acN2Hs);
        bigList.add(exN2Ls);
        bigList.add(acN2Ls);
        bigList.add(exT2Hs);
        bigList.add(acT2Hs);
        bigList.add(exT2Ls);
        bigList.add(acT2Ls);
        // write results to file for python script to plot
        try (PrintWriter writer = new PrintWriter(new FileWriter("results"))) {
            for (List<Float> floatList : bigList) {
                for (float number : floatList) {
                    writer.print(number + " "); // Separate floats within a list with a space
                }
                writer.println(); // one list per line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}