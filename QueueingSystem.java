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

    void run() {
        int H1, H2, L1, L2;
        float clock = 0;
        ArrayList<Event> elist;
        boolean done;

        // TODO: run
        // int numDep = 0, numArr = 0, numBlock = 0;
        // // area = area under the graph of number of customers in system vs time
        // float area = 0;
        // // utilization
        // this.actualUtil = 0;
        // // both machines produce components to begin
        // this.insertEvent(new Event(EventType.ARR1, Exponential.get(this.gamma)));
        // this.insertEvent(new Event(EventType.ARR2, Exponential.get(this.getLambda())));
        // while (!this.done) {
        //     // pop off the event list, update state
        //     Event currEvent = this.elist.remove(0);
        //     area += this.N * (currEvent.time - this.clock);
        //     if (this.N == 1)
        //         this.actualUtil += (currEvent.time - this.clock) / 2;
        //     else if (this.N >= 2)
        //         this.actualUtil += (currEvent.time - this.clock);
        //     this.clock = currEvent.time;
        //     // handle event
        //     switch (currEvent.type) {
        //         case ARR1:
        //             if (this.N >= 2) { 
        //                 // discard i.e. block but dont count for simulation
        //                 // generate next arrival
        //                 this.insertEvent(new Event(EventType.ARR1, this.clock + Exponential.get(this.gamma)));
        //             } else {
        //                 this.N++;
        //                 numArr++;
        //                 // generate next arrival
        //                 this.insertEvent(new Event(EventType.ARR1, this.clock + Exponential.get(this.gamma)));
        //                 if (this.N <= this.m) // service
        //                     this.insertEvent(new Event(EventType.DEP, this.clock + Exponential.get(this.mu)));
        //             }
        //             break;
        //         case ARR2:
        //             if (this.N == this.K) {
        //                 // discard i.e. block
        //                 numBlock++;
        //                 // generate next arrival
        //                 this.insertEvent(new Event(EventType.ARR2, this.clock + Exponential.get(this.getLambda())));
        //             } else {
        //                 this.N++;
        //                 numArr++;
        //                 // generate next arrival
        //                 this.insertEvent(new Event(EventType.ARR2, this.clock + Exponential.get(this.getLambda())));
        //                 if (this.N <= this.m) // service
        //                     this.insertEvent(new Event(EventType.DEP, this.clock + Exponential.get(this.mu)));
        //             }
        //             break;
        //         case DEP:
        //             numDep++;
        //             this.N--;
        //             if (this.N >= this.m) // service next customer
        //                 this.insertEvent(new Event(EventType.DEP, this.clock + Exponential.get(this.mu)));
        //             break;
        //     }
        //     if (numDep > 100000)
        //         this.done = true;
        // }

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
        if (args.length != 8)
            throw new IllegalArgumentException("usage: java QueueingSystem K m µ");

        float pH, pL, r2D, r21, r22, mu1, mu2H, mu2L;
        pH = Float.parseFloat(args[0]);
        pL = Float.parseFloat(args[1]);
        r2D = Float.parseFloat(args[2]);
        r21 = Float.parseFloat(args[3]);
        r22 = Float.parseFloat(args[4]);
        mu1 = Float.parseFloat(args[5]);
        mu2H = Float.parseFloat(args[6]);
        mu2L = Float.parseFloat(args[7]);

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