package com.game.alberto.tetris;

/**
 * Created by armando on 11/12/14.
 */
public class Tetris {

    int[][] occupied = new int[10][20];
    int score=0;  // score
    int lineCompleted = 0;   // number of lines completed
    int level=0;
    int velocidad;


    //FallingToken
    int x,y;
    int tokenNumber, rotationNumber;
    int[] xArray;
    int[] yArray;
    boolean gameOver=false;
    int delay=3;

    //shiftDown
    int frame=0;
    boolean reachFloor=false;


    //KeyPressed
    boolean leftPressed=false;
    boolean rightPressed=false;
    boolean downPressed=false;
    boolean spacePressed=false;
    boolean pause=false;


    //node




    Tetris (){

    }

    static int[][][] xRotationArray = {
            { {0,0,1,2}, {0,0,0,1}, {2,0,1,2}, {0,1,1,1} },  // token number 0
            { {0,0,1,1}, {1,2,0,1}, {0,0,1,1}, {1,2,0,1} },  // token number 1
            { {1,1,0,0}, {0,1,1,2}, {1,1,0,0}, {0,1,1,2} },  // token number 2
            { {0,1,2,2}, {0,1,0,0}, {0,0,1,2}, {1,1,0,1} },  // token number 3
            { {1,0,1,2}, {1,0,1,1}, {0,1,1,2}, {0,0,1,0} },  // token number 4
            { {0,1,0,1}, {0,1,0,1}, {0,1,0,1}, {0,1,0,1} },  // token number 5
            { {0,1,2,3}, {0,0,0,0}, {0,1,2,3}, {0,0,0,0} }   // token number 6
    };

    static int[][][] yRotationArray = {
            { {0,1,0,0}, {0,1,2,2}, {0,1,1,1}, {0,0,1,2} },  // token number 0
            { {0,1,1,2}, {0,0,1,1}, {0,1,1,2}, {0,0,1,1} },  // token number 1
            { {0,1,1,2}, {0,0,1,1}, {0,1,1,2}, {0,0,1,1} },  // token number 2
            { {0,0,0,1}, {0,0,1,2}, {0,1,1,1}, {0,1,2,2} },  // token number 3
            { {0,1,1,1}, {0,1,1,2}, {0,0,1,0}, {0,1,1,2} },  // token number 4
            { {0,0,1,1}, {0,0,1,1}, {0,0,1,1}, {0,0,1,1} },  // token number 5
            { {0,0,0,0}, {0,1,2,3}, {0,0,0,0}, {0,1,2,3} }   // token number 6
    };



    public void drawCell(int x,int y){
        occupied[x][y] = 1;
    }


    public void eraseCell(int x,int y){
        occupied[x][y] = 0;
    }


    public void drawToken(int x, int y, int[] xArray, int[] yArray){
        for (int i=0;i<4;i++)
        {
            drawCell(x+xArray[i],y+yArray[i]);
        }
    }


    public void eraseToken(int x, int y, int[] xArray, int[] yArray){
        for (int i=0;i<4;i++)
        {
            eraseCell(x+xArray[i],y+yArray[i]);
        }
    }



    public boolean isValidPosition(int x,int y, int tokenNumber, int rotationNumber)
    {
        int[] xArray1 = xRotationArray[tokenNumber][rotationNumber];
        int[] yArray1 = yRotationArray[tokenNumber][rotationNumber];

        for (int i=0;i<4;i++)  // loop over the four cells
        {
            int xCell = x+xArray1[i];
            int yCell = y+yArray1[i];

            // range check
            if (xCell<0) return false;
            if (xCell>=10) return false;
            if (yCell<0) return false;
            if (yCell>=20) return false;

            // occupancy check
            if (occupied[xCell][yCell]==1) return false;
        }
        return true;
    }

    public int[] checkRowCompletion()
    {
        int[] complete1 = new int[20];
        for (int y1=0;y1<20;y1++)  // 20 rows
        {
            int filledCell = 0;
            for (int x1=0;x1<10;x1++)  // 10 columns
            {
                if (occupied[x1][y1]==1) filledCell++;
                if (filledCell==10) // row completed
                {
                    complete1[y1]=1;
                }
            }
        }
        return complete1;

    }

    public void clearCompleteRow(int[] completed)
    {
        // must loop for odd number of times.
        // toggle sequence : 0,1,0,1,0
        for (int blinking=0;blinking<5;blinking++)
        {
            for (int i=0;i<completed.length;i++)
            {
                if (completed[i]==1)
                {
                    for (int x1=0;x1<10;x1++)
                    {
                        // toggle the occupancy array
                        occupied[x1][i]=1-occupied[x1][i];
                    }
                }
            }
        }
    }


    public void setVel(int vel){
        this.velocidad=vel;
    }

    public void shiftDownComplete(int[] completed)
    {
        for (int row=0;row<completed.length;row++)
        {
            if (completed[row]==1)
            {
                for (int y1=row;y1>=1;y1--)
                {
                    for (int x1=0;x1<10;x1++)
                    {
                        occupied[x1][y1] = occupied[x1][y1-1];
                    }
                }
            }
        }
    }


    void addScore(int[] complete){

        int bonus=10;  // score for the first completed line
        for (int row=0;row<complete.length;row++){
            if (complete[row]==1){
                lineCompleted += 1;
                score+=bonus;
                bonus*=2;  // double the bonus for every additional line
            }
        }

        level = lineCompleted/3;
        if (level>30) { lineCompleted=0; level=0; }  // MAX LEVEL

    }




    public void FallingToken(){


        x=5;y=0;


        tokenNumber = tokenNumber1;
        rotationNumber = (int) (4*Math.random());



        xArray = xRotationArray[tokenNumber][rotationNumber];
        yArray = yRotationArray[tokenNumber][rotationNumber];

        if (!isValidPosition(x,y,tokenNumber,rotationNumber))
        {
            gameOver=true;
            drawToken(x,y,xArray,yArray);
            return;
        }

        drawToken(x,y,xArray,yArray);
    }



    int x2=0,xn2=0;
    //!reachFloor
    public void shiftDown(){

        eraseToken(x,y,xArray,yArray);

        // add keyboard control
        if (leftPressed && isValidPosition(x-1,y,tokenNumber,rotationNumber))xn2++; if(xn2==3){x-= 1;xn2=0;}
        if (rightPressed && isValidPosition(x+1,y,tokenNumber,rotationNumber)) x2++;if(x2==3){x += 1;x2=0;}

        if(downPressed) {
            for (int i = 0; i < 20; i++) {
                if (isValidPosition(x, y + 1, tokenNumber, rotationNumber)) y += 1;
                spacePressed=false;
            }
            aux=true;
        }
        downPressed=false;
        if (spacePressed && isValidPosition(x,y,tokenNumber,(rotationNumber+1)%4))
        {
            rotationNumber = (rotationNumber+1)%4;
            xArray = xRotationArray[tokenNumber][rotationNumber];
            yArray = yRotationArray[tokenNumber][rotationNumber];
            spacePressed=false;
        }

        int f=31-level;   // fall for every 31 frames, this value is decreased when level up
        if (frame % f==0) y += 1;
        if (!isValidPosition(x,y,tokenNumber,rotationNumber)) // reached floor
        {
            reachFloor=true;
            y -= 1;  // restore position
        }
        drawToken(x,y,xArray,yArray);
        frame++;
    }


    public void printGameOver()
    {
        System.out.print("Perdedor");
    }

    int cont=0;
    int [] complete = null;
    int tokenNumber1;
    int cont1=0;
    boolean aux=false;
    public void tetris(){

        if(cont1==0){
            tokenNumber1 = (int) (7*Math.random());
            cont1=1;
        }
        if (!gameOver&&!pause){

            if(cont==0){


                FallingToken();
                cont++;
                tokenNumber1 = (int) (7*Math.random());
                System.out.print(tokenNumber1);

            }
            else if(cont==1){
                if(!reachFloor&&!aux){
                    try { Thread.sleep(delay); } catch (Exception ignore) {}
                    shiftDown();
                }
                else{
                    cont++;
                    reachFloor=false;
                    aux=false;
                }
            }
            else if(cont==2){
                complete=checkRowCompletion();
                clearCompleteRow(complete);
                addScore(complete);
                cont++;
            }
            else if(cont==3){
                try { Thread.sleep(100); } catch (Exception ignore) {}
                shiftDownComplete(complete);
                cont=0;
            }
            else{

            }


            paint();
        }else {
            if(gameOver){
                printGameOver();
            }
            else if(pause){
                //pausa
            }
        }



    }




    public void paint(){



        // matrix();
    }










}

