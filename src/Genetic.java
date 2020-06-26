import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Genetic {
    public static double[] init() {
        Random r = new Random();
        double[] arr = new double[10];
        for (int i = 0; i < 10; i++) {
            arr[i] = 70 * (r.nextDouble()) - 35;
            //nextDouble 은 범위가 0-1까지 , 위의 식은 -30 부터 +30 까지의 범위를 말한다.
        }
        return arr;
    }

    public static double[] initAonly() {
        Random r = new Random();
        double[] arr = new double[10];
        for (int i = 0; i < 10; i++) {
            arr[i] = 2*(r.nextDouble()) - 1;
            //nextDouble 은 범위가 0-1까지 , 위의 식은 -30 부터 +30 까지의 범위를 말한다.
        }
        return arr;
    }

    // yhat 구하기
    public static double[] erf(double a, double b, double c, double[] x, double[] y) {
        double[] tmp = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            tmp[i] = a*Math.pow((x[i]-b),2) + c;
        }
        return tmp;
    }
    //오차값들의 합 (단일 double값 출력)
    public static double costf(double[] yhat, double[] y) {
        double mse = 0;
        for (int i = 0; i < y.length; i++) {
            mse += Math.pow(y[i] - yhat[i], 2);
        }
        return mse;
    }
    // 교배  (init개수만큼 배열 생성)
    public static double[] crossover(double[] x) {
        double[] arr = new double[x.length];
        for (int i = 0; i < x.length; i += 2) {
            arr[i] = x[i]/4*3+ x[i + 1]/4;
            arr[i + 1] =x[i]/4 + x[i+1]/4*3;
        }

        return arr;
    }

    //돌연변이 ( 2베 , 0.8배 생성 / 20프로변형)
    public static double[] mutation(double[] x) {
        double[] arr = new double[x.length];
        for (int i = 0; i < 3; i += 2) {
            arr[i] = 1.2 * x[i];
            arr[i + 1] = x[i + 1]/2;
        }
        if (x.length - 4 >= 0) System.arraycopy(x, 4, arr, 4, x.length - 4);

        return arr;
    }


    //A값 설정, b와c는 고정 , 오차합 최소가 되는 값 찾기
    public static double[] selectA(double[] a, double b, double c, double[] x, double[] y) {
        double[] cost = new double[a.length];
        double min = Double.MAX_VALUE;
        double[] ck = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            double[] yhat = erf(a[i], b, c, x, y);
            double mse = costf(yhat, y);

            if (min > mse) {
                min = mse;
                ck[i] = a[i];
            } else {
                ck[i] = ck[i-1];
            }
        }
        return ck;
    }


    //B값 설정, a와b는 고정 , 오차합 최소가 되는 값 찾기
    public static double[] selectB(double a, double[] b, double c, double[] x, double[] y) {
        double[] cost = new double[b.length];
        double min = Double.MAX_VALUE;
        double[] ck = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            double[]  yhat=erf(a,b[i],c,x,y);
            double mse = costf(yhat, y);

            if (min > mse) {
                min = mse;
                ck[i] = b[i];
            }else{
                ck[i]=ck[i-1];
            }
        }
        return ck;
    }


    //C값 설정, a와b는 고정 , 오차합 최소가 되는 값 찾기
    public static double[] selectC(double a, double b, double[] c, double[] x, double[] y) {
        double[] cost = new double[c.length];
        double min = Double.MAX_VALUE;
        double[] ck = new double[c.length];
        for (int i = 0; i < c.length; i++) {
            double[] yhat = erf(a, b, c[i], x, y);
            double mse = costf(yhat, y);

            if (min > mse) {
                min = mse;
                ck[i] = c[i];
            } else {
                ck[i] = ck[i - 1];
            }
        }

        return ck;
    }


    //update된 a,b,c값 오차 확인 + 출력
    public static void selectAll(double upa, double upb, double upc ,double[] x,double[] y){

        double[] yhat = erf(upa, upb, upc, x, y);
        double mse = costf(yhat, y)/1000000;//오차합
        System.out.printf("min=%f a=%f b=%f c=%f",mse,upa,upb,upc);
        System.out.println();
    }



    //Main함수
    public static void main(String[] args) {
        double[] x = new double[80];
        double[] y = new double[80];
        try {
            BufferedReader br = new BufferedReader(new FileReader("bmi_age.csv"));
            String line;
            int n = 0;
            while ((line = br.readLine()) != null) {
                if (n == 0) {
                    n++;
                    continue;
                }

                String[] str = line.split(",");
                y[n - 1] = Double.parseDouble(str[1]);
                x[n - 1] = Double.parseDouble(str[2]);
                n++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // data값 읽어오기
        for (int i = 0; i < x.length; i++) {
            System.out.printf("(%f %f)\n", x[i], y[i]);
        }




        //Random한 a,b,c 배열 생성  ( 길이 10)
        double[] initA=initAonly();
        double[] initB=init();
        double[] initC=init();


        // update된 a,b,c값 담을 배열
        double[] upc = new double[initC.length];
        double[] upb = new double[initB.length];
        double[] upa = new double[initA.length];



        //iteration for문 (10번 돌릴거야)
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i <initA.length; i++)  // init한 개체만큼 for문
            {
                double[] sxc = selectC(initA[i], initB[i], initC, x, y);
                double[] cxc = crossover(sxc);
                upc =mutation(cxc);   //c값 업데이트

                double[] sxb = selectB(initA[i],initB,upc[i],x,y);
                double[] cxb = crossover(sxb);
                upb = mutation(cxb); //업데이트된 c값기준으로 b값 업데이트

                double[] sxa= selectA(initA, upb[i],upc[i],x,y);
                double[] cxa= crossover(sxa);
                upa = mutation(cxa); //업데이트된 b,c값 기준으로 a값 업데이트트


                //update된 a,b,c값 출력
                for (int l = 0; l <initA.length ; l++) {
                    selectAll(upa[l],upb[l],upc[l],x,y);
                }
                initA=upa;
                initB=upb;
                initC=upc;

            }
        }



    }


}