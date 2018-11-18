import java.io.*;
import java.util.*;
public class Main {

    static String[] states, alpha, initst, finst, trans;
    static String e3Word;
    static int initstNumber;
    static  int[] finstNumber;
    public static void main(String[] args)throws IOException {
        // 1.1 Putting all lines to separate arrays
        // 1.2 Checking e5
        // 2. Checking e1
        // 3. Creating graph by matrix
        // 4. Checking e2,e3,e4
        // 5. Checking completeness
        // 6. Checking dor warnings
        Scanner scanner = new Scanner(new File("fsa.txt"));
        PrintWriter printWriter = new PrintWriter(new File("result.txt"));

        String statesLine = scanner.nextLine();
        if (!checkE5States(simpleLine(statesLine))){
            states = simpleLine(statesLine);
        } else {
            printWriter.println("Error:");
            printWriter.print("E5: Input file is malformed");
            printWriter.close();
            return;
        }
        String alphaLine = scanner.nextLine();
        if (!checkE5Alpha(simpleLine(alphaLine))) {
            alpha = simpleLine(alphaLine);
        }
        else{
            printWriter.println("Error:");
            printWriter.print("E5: Input file is malformed");
            printWriter.close();
            return;
        }

        String initstLine = scanner.nextLine();
        initst = simpleLine(initstLine);

        String finstLine = scanner.nextLine();
        finst = simpleLine(finstLine);
        finstNumber = new int[finst.length];

        String transLine =  scanner.nextLine();
        trans = simpleLine(transLine);


            //checking for e1,e2,e3,e4
        if (checkE1(states,initst)){
            printWriter.println("Error:");
            printWriter.print("E1: A state '"+initst[0]+"' is not in set of states");
            printWriter.close();
            return;
        }
        String [][] graph = createGraph(states,trans,initst,finst);
        if (checkE2(graph)){
            printWriter.println("Error:");
            printWriter.print("E2: Some states are disjoint");
            printWriter.close();
            return;
        }
        if (checkE3(alpha,trans)){
            printWriter.println("Error:");
            printWriter.print("E3: A transition '"+e3Word+"' is not represented in the alphabet");
            printWriter.close();
            return;
        }
        if (checkE4(initst)){
            printWriter.println("Error:");
            printWriter.print("E4: Initial state is not defined");
            printWriter.close();
            return;
        }

//        //checking for completeness
//        if (checkCompleteness(graph,alpha)){
//            printWriter.print("FSA is complete");
//        } else {
//            printWriter.print("FSA is incomplete");
//        }

        if (checkE6(graph)) {
            printWriter.println("Error:");
            printWriter.print("E6: FSA is nondeterministic");
            printWriter.close();
            return;
        }
        //kleeneAlgAllSteps(graph,initst,finst);
        scanner.close();
        printWriter.close();
    }

    private static String[] simpleLine(String str){
        //Parsing each line for extract values
            String newStr = str.substring(str.indexOf("{") + 1, str.indexOf("}"));
            int n = 0;
            for (int i = 0; i < newStr.length(); i++) {
                if (newStr.substring(i, i + 1).equals(","))
                    n++;
            }
            String[] resultArr = newStr.split(",");
            return resultArr;
    }


    private static boolean checkE1 (String[] states, String[] initst){
        // Searching init st in set of all states
        // if we will not find it in set we return true
        if(!initst[0].equals("")) {
            for (int i = 0; i < states.length; i++) {
                if (states[i].equals(initst[0]))
                    return false;
            }
            return true;
        } else
            return false;
    }
    private static boolean checkE2 (String[][] graph){
        //We take our state and look does he have connection to other states

        for (int i = 0; i <graph.length ; i++) {
            boolean checkConnec = false;
            for (int j = 0; j <graph[0].length ; j++) {
                if (i!=j) {
                    if (graph[i][j] != null) {
                        checkConnec = true;
                        break;
                    }
                } else if (graph.length==1)
                    checkConnec = true;
            }
            if (!checkConnec) {
                boolean checkConnec2 = false;
                for (int j = 0; j <graph.length ; j++) {
                    if(i!=j) {
                        if (graph[j][i] != null) {
                            checkConnec2 = true;
                            break;
                        }
                    } else if (graph.length==1)
                        checkConnec2 = true;
                }
                if (!checkConnec2)
                    return true;

            }

        }
        return false;
    }
    private static boolean checkE3 (String[] alpha, String[] trans){
        //Searching for each transition in every element of alphabet
        for (int i = 0; i <trans.length ; i++) {
            boolean checkExist = false;
            for (int j = 0; j <alpha.length ; j++) {
                if (alpha[j].equals(parseTrans(trans[i])[1])){
                    checkExist=true;
                    break;
                }
            }
            if (!checkExist) {
                e3Word = parseTrans(trans[i])[1];
                return true;
            }
        }
        return false;
    }
    private static boolean checkE4 (String[] initst){
        //if init st are empty we return true
        if(!initst[0].equals("")) {
            return false;
        } else
            return true;
    }

    private static boolean checkCompleteness (String[][] graph, String[] alpha){
        // 0.Copy alphabet in alphaList and making graphRow, - here will be transitions of each state
        // 1.Adding in graphRow[] all transition from each state, but without duplicates
        // 2.1.Searhing for each transition in alphabet
        // 2.2. If we find it, we delete this transition in graphRow and in alphaList
        // 3. If alphaList isnt empty it means that state don't use all transition, and we return false(incomplete)
        // 4. We checking all rows.
        // 5. And after checking all rows(states) we will return true(complete)

        ArrayList<String> graphRow = new ArrayList<>();
        ArrayList<String> alphaList = new ArrayList<>();

        for (int i = 0; i < graph.length ; i++) {
            for (int j = 0; j < graph.length; j++) {
                if (graph[i][j]!=null) {//если действие есть
                    if (!graph[i][j].contains(" ")) {//if only one move
                        if (!graphRow.contains(graph[i][j])) {
                            graphRow.add(graph[i][j]);
                        }
                    } else { // if more than one move
                        String[] moreAlp = graph[i][j].split(" ");
                        for (int k = 0; k < moreAlp.length; k++) {
                            if (!graphRow.contains(moreAlp[k])) {
                                graphRow.add(moreAlp[k]);
                            }
                        }
                    }
                }
            }

            Collections.addAll(alphaList,alpha);
            int j=0;
            while (!graphRow.isEmpty()){
                if (alphaList.contains(graphRow.get(j))){
                    String tmpDelete=graphRow.get(j);
                    while (graphRow.contains(tmpDelete))
                        graphRow.remove(tmpDelete);
                    alphaList.remove(tmpDelete);
                } else {
                    j++;
                }
            }
            if (!alphaList.isEmpty())
                return false;
        }

        return true;
  }

    private static boolean checkW1 (String[] finst){
        //if fin st are empty,we return true
        if (finst[0].equals(""))
            return true;
        return false;
    }
    private static boolean checkW2 (String[][] graph,String[] initst,String[] states){
        // 0.1 initNum its a init st
        // 0.2 creating isVisited - here I will write was I in each state ot not
        // 1. Use dfs to check all states
        // 2. If at least element in isVisited will equal false we return true, because we didnt reach one(or more) state from init state
        int initNum = 0;
        for (int i = 0; i <states.length ; i++) {
            if (states[i].equals(initst[0])){
                initNum=i;
                break;
            }
        }
        boolean[] isVisited = new boolean[states.length];
        System.arraycopy(dfsForStates(graph,isVisited,initNum),0,isVisited,0,dfsForStates(graph,isVisited,initNum).length);
        for (int i = 0; i <isVisited.length ; i++) {
            if (isVisited[i] == false)
                return true;
        }
        return false;
    }
    private static boolean[] dfsForStates(String[][] graph, boolean[] isVisited,int i){
        //1.Write in isVisited
        //2.Look can I go to another states from current state
        //3.Check visited or not state were I want to go
        //4.If not visited I go to this state
        //5.If I checked all states from current state I return to previous state with isVisited
        //
        isVisited[i] = true;
        for (int k = 0; k <states.length ; k++) {
            if (graph[i][k]!= null){
                if (isVisited[k] != true){
                    System.arraycopy(dfsForStates(graph,isVisited,k),0,isVisited,0,dfsForStates(graph,isVisited,k).length);
                }
            }
        }
        return isVisited;
    }

    private static boolean checkE6(String[][] graph){
        //1.I add all transitions of current state to list
        //2.If I meet duplicate, I return true(nondeterministic)
        for (int i = 0; i <graph.length ; i++) {
            ArrayList<String> arr = new ArrayList();
            for (int j = 0; j <graph.length ; j++) {
                if (graph[i][j]!=null){
                    if (arr.contains(graph[i][j])){
                        return true;
                    } else{
                        arr.add(graph[i][j]);
                    }
                }
            }
        }
        return false;
    }

    private static String[][] createGraph(String[] states,String[] trans,String[] initst, String[] finst){
        //1.Im creating square matrix of size of states
        //Each row are state, and each column are state where you go from state in row
        //Elements in matrix are transitions
        //2.Parsing transitions, take starting state, transition, final state
        //3.Starting state - row, final state - column, transition in their coordinates
        String[][] graph = new String[states.length][states.length];
        for (int i = 0; i <trans.length ; i++) {
            String[] tmp =parseTrans(trans[i]);
            if (trans[i].substring(0,trans[i].indexOf(">")).equals(initst[0])){
                initstNumber = Integer.parseInt(tmp[0]);
            }
            int tempInt =0;
            for (int j = 0; j <finst.length ; j++) {
                if (trans[i].substring(0,trans[i].indexOf(">")).equals(finst[j])){
                    finstNumber[tempInt] = Integer.parseInt(tmp[0]);
                    tempInt++;
                }
            }
            if(graph[Integer.parseInt(tmp[0])][Integer.parseInt(tmp[2])]==null) {
                graph[Integer.parseInt(tmp[0])][Integer.parseInt(tmp[2])] = tmp[1];
            } else
                graph[Integer.parseInt(tmp[0])][Integer.parseInt(tmp[2])] = graph[Integer.parseInt(tmp[0])][Integer.parseInt(tmp[2])]+" "+ tmp[1];
        }
        /*for (int i = 0; i <graph.length ; i++) {
            for (int j = 0; j <graph.length ; j++) {
                System.out.print(graph[i][j]+" ");
            }
            System.out.println("-");
        }*/
        return graph;
    }

    private static String[] parseTrans(String trans){
        //Parsing each transition
        //1.starting state in 0 element
        //2.transition in 1 element
        //3.final state in 2 element
        String[] parsedTrans= new String[3];
        parsedTrans[0] = trans.substring(0,trans.indexOf(">"));

        for (int i = 0; i <states.length ; i++)
            if (states[i].equals(parsedTrans[0]))
                parsedTrans[0]= String.valueOf(i);

        trans=trans.substring(trans.indexOf(">")+1);
        parsedTrans[1] = trans.substring(0,trans.indexOf(">"));
        parsedTrans[2] = trans.substring(trans.indexOf(">")+1);

        for (int i = 0; i <states.length ; i++)
            if (states[i].equals(parsedTrans[2]))
                parsedTrans[2]= String.valueOf(i);

        return parsedTrans;
    }

    //E5 checking input format by ASCII
    private static boolean checkE5States(String[]states){
        for (int i = 0; i <states.length ; i++) {
            for (int j = 0; j <states[i].length() ; j++) {
                int charNum=(int)states[i].charAt(j);
                if (!(charNum <= 122 && charNum >= 97
                        ||charNum <= 90 && charNum >= 65
                        ||charNum < 57 && charNum >= 48)){
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkE5Alpha(String[]states){
        for (int i = 0; i <states.length ; i++) {
            for (int j = 0; j <states[i].length() ; j++) {
                int charNum=(int)states[i].charAt(j);
                if (!(charNum <= 122 && charNum >= 97
                        ||charNum <= 90 && charNum >= 65
                        ||charNum < 57 && charNum >= 48
                        ||charNum == 95)){
                    return true;
                }
            }
        }
        return false;
    }

//    private static String kleeneAlgAllSteps(String[][] graph){
//        String[][] rGraph = kleeneAlgStep0(graph);
//        for (int i = 0; i <graph.length ; i++) {
//            for (int j = 0; j <graph.length ; j++) {
//
//            }
//        }
//
//    }

    private static String[][] kleeneAlgStep0(String[][] graph){
        String[][] rGraph = new String[graph.length][graph.length];
        for (int i = 0; i < graph.length ; i++) {
            for (int j = 0; j < graph.length ; j++) {
                rGraph[i][j] = parseTransFromGraph(graph[i][j]);
                if (i==j){
                    if (rGraph[i][j]!="")
                        rGraph[i][j]=rGraph[i][j] + " | eps";
                    else
                        rGraph[i][j]=rGraph[i][j] + "eps";
                }else if (rGraph[i][j]==""){
                    rGraph[i][j]=rGraph[i][j] + "{}";
                }

                System.out.print("["+i+"]"+"["+j+"]"+rGraph[i][j]+" ");
            }
            System.out.println("");
        }
        return rGraph;
    }
    private static String parseTransFromGraph(String str){
        if(str!=null) {
            String[] tempArr = str.split(" ");
            String returnStr = tempArr[0];
            for (int i = 1; i < tempArr.length; i++) {
                returnStr = returnStr + " | " + tempArr[i];
            }
            return returnStr;
        } else
            return "";

    }
}