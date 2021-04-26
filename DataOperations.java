import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

//Implements 4 data searching operations on swing data
class DataOperations {

    static final String COMMA_DELIMITER = ",";

    //creates data structure for aiding with search operations
    static class Data {

        List<List<String>> data;
        double[] ax;
        double[] ay;
        double[] az;
        double[] wx;
        double[] wy;
        double[] wz;

        public Data( String fileName ) {

            try{
                data = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(COMMA_DELIMITER);
                        data.add(Arrays.asList(values));
                    }
                }
                ax = new double[data.size()];
                ay = new double[data.size()];
                az = new double[data.size()];
                wx = new double[data.size()];
                wy = new double[data.size()];
                wz = new double[data.size()];
                for( int i = 0; i < data.size(); i++ ) {

                    ax[i] = Double.parseDouble(data.get(i).get(1));
                    ay[i] = Double.parseDouble(data.get(i).get(2));
                    az[i] = Double.parseDouble(data.get(i).get(3));
                    wx[i] = Double.parseDouble(data.get(i).get(4));
                    wy[i] = Double.parseDouble(data.get(i).get(5));
                    wz[i] = Double.parseDouble(data.get(i).get(6));

                }
            } catch(Exception e) {
                e.printStackTrace();
            }

        }

    }

    /*
    Function: Helper Search method for searching through data
    @param data1 (double[]): the first data we will be searching through
    @param data2 (double[]): the second data we will be searching through
    @param begin (int): the index we will begin searching through
    @param end (int): the index we will end our search
    @param thresh1 (double): the threshold for our data values to be higher than
    @param thresh2 (double): the threshold for our data values to be lower than
    @param winLength (int): the amount of datapoints in a row that need to be higher
                            than threshold
    @param forawrd (boolean): boolean for if we aresearching through the list in the forwards direction
    @param between (boolean): boolean for if we consider both thresholds
    @param multiData (boolean): boolean for if we are searching through multiple datas
    @return int: the index of the first datapoint which starts a series as long as
                winLength of datapoints which exceed threshold
    */
    public static int searchHelper( double[] data1, double[] data2, int begin, int end, double thresh1,
        double thresh2, int winLength, boolean forward, boolean between, boolean multiData ) {

        if( data1 == null || data2 == null || begin < 0 || begin >= data1.length || begin >= data2.length ||
            begin > end || end < 0 || end >= data1.length || end >= data2.length || end - begin < winLength ) {
                return -999;
        }

        int startIndex;
        int endIndex;
        if( forward ) {
            startIndex = begin;
            endIndex = end;
        } else {
            startIndex = end;
            endIndex = begin;
        }

        int foundAbove = 0;
        int start = startIndex;
        while( startIndex != endIndex ) {
            if( forward && !between && !multiData ) {
                if( data1[startIndex] > thresh1 ) {
                    foundAbove++;
                    if( foundAbove >= winLength ) {
                        return start;
                    }
                } else {
                    foundAbove = 0;
                    start = startIndex + 1;
                }
                startIndex++;
            } else if( !forward && between && !multiData ) {
                if( data1[startIndex] > thresh1 && data1[startIndex] < thresh2 ) {
                    foundAbove++;
                    if( foundAbove >= winLength ) {
                        return start;
                    }
                } else {
                    foundAbove = 0;
                    start = startIndex - 1;
                }
                startIndex--;
            } else if( forward && !between && multiData  ) {
                if( data1[startIndex] > thresh1 && data2[startIndex] > thresh2 ) {
                    foundAbove++;
                    if( foundAbove >= winLength ) {
                        return start;
                    }
                } else {
                    foundAbove = 0;
                    start = startIndex + 1;
                }
                startIndex++;
            } else if( forward && between && !multiData ) {
                if( data1[startIndex] > thresh1 &&  data1[startIndex] < thresh2 ) {
                    foundAbove++;
                    if( foundAbove >= winLength ) {
                        return start;
                    }
                } else {
                    foundAbove = 0;
                    start = startIndex + 1;
                }
                startIndex++;
            }
        }
        return -1;
    }

    /*
    Function: Search through data forwards for values that are higher than threshold
    @param data (double[]): the data we will be searching through
    @param indexBegin (int): the index we will begin searching through
    @param indexEnd (int): the index we will end our search
    @param threshold (double): the threshold for our data values to be higher than
    @param winLength (int): the amount of datapoints in a row that need to be higher
                            than threshold
    @return int: the index of the first datapoint which starts a series as long as
                winLength of datapoints which exceed threshold
    */
    public static int searchContinuityAboveValue( double[] data, int indexBegin, int indexEnd,
        double threshold, int winLength ) {

        return searchHelper( data, data, indexBegin, indexEnd, threshold, 0, winLength, true, false, false );

    }

    /*
    Function: Search through backwards for values that are higher than threshold
    @param data (double[]): the data we will be searching through
    @param indexBegin (int): the index we will begin searching through
    @param indexEnd (int): the index we will end our search
    @param thresholdLo (double): the threshold for our data values to be higher than
    @param thresholdHi (double): the threshold for our data values to be lower than
    @param winLength (int): the amount of datapoints in a row that need to be higher
                            than threshold
    @return int: the index of the first datapoint which starts a series as long as
                winLength of datapoints which exceed threshold
    */
    public static int backSearchContinuityWithinRange( double[] data, int indexBegin,
        int indexEnd, double thresholdLo, double thresholdHi, int winLength ) {

        return searchHelper( data, data, indexEnd, indexBegin, thresholdLo, thresholdHi, winLength, false, true, false );

    }

    /*
    Function: Search through two sets of values for values that are higher than threshold
    @param data1 (double[]): the first data we will will be searching through
    @param data2 (double[]): the second data we will be searching through
    @param indexBegin (int): the index we will begin searching through both datas
    @param indexEnd (int): the index we will end searching through both datas
    @param threshold1 (double): the threshold for our data1 values to be higher than
    @param threshold2 (double): the threshold for our data2 values to be higher than
    @param winLength (int): the amount of datapoints in a row that need to be higher
    @return int: the index of the first datapoint which starts a series as long as
                winLength of datapoints which exceed threshold
    */
    public static int searchContinuityAboveValueTwoSignals( double[] data1, double[] data2,
        int indexBegin, int indexEnd, double threshold1, double threshold2, int winLength ) {

        return searchHelper( data1, data2, indexBegin, indexEnd, threshold1, threshold2, winLength, true, false, true );

    }

    /*
    Function: Search through data forwards for values that are higher than
            thresholdLo and lower than thresholdHi
    @param data (double[]): the data we will be searching through
    @param indexBegin (int): the index we will begin searching through
    @param indexEnd (int): the index we will end our search
    @param thresholdLo (double): the threshold for our data values to be higher than
    @param thresholdHi (double): the threshold for our data values to be lower than
    @param winLength (int): the amount of datapoints in a row that need to be higher
                            than threshold
    @return int[]: the entries that meet winlength and are between both thresholds
    */
    public static int[] searchMultiContinuityWithinRange( double[] data, int indexBegin,
    int indexEnd, double thresholdLo, double thresholdHi, int winLength) {

        List<Integer> samples = new ArrayList();

        if( data == null || indexBegin < 0 || indexBegin >= data.length ||
                indexBegin > indexEnd || indexEnd < 0 || indexEnd >= data.length ||
                indexEnd - indexBegin < winLength || thresholdHi < thresholdLo ) {
                    return new int[0];
        }

        int found = searchHelper( data, data, indexBegin, indexEnd, thresholdLo, thresholdHi, winLength, true, true, false );
        while( found != -1 && found != -999 ){
            for( int i = found; i <= found + winLength; i++ ) {
                samples.add( new Integer(i) );
            }
            found = searchHelper( data, data, found + winLength, indexEnd, thresholdLo, thresholdHi, winLength, true, true, false );
        }
        int[] sampleList = new int[samples.size()];
        for( int i = 0; i < samples.size(); i++ ) {
            sampleList[i] = samples.get(i);
        }

        return sampleList;

    }

    public static void main( String[] args ) {

        //Testing each function

        Data data = new Data("latestSwing.csv");

        System.out.println( searchContinuityAboveValue( data.ax, 20, 40, 1.0, 2 ) == 37);

        System.out.println( backSearchContinuityWithinRange( data.ay, 40, 0, 0, 1, 3 ) == 20);

        System.out.println( searchContinuityAboveValueTwoSignals( data.ax, data.ay, 10, 40, 0.5, 1.2, 2 ) == 33);

        int[] test = searchMultiContinuityWithinRange( data.ax, 10, 30, 0, 1, 5 );
        System.out.println(test[0] == 20);
        System.out.println(test[10] == 29);
    }

}