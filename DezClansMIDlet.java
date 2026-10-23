package com.claselementares;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import java.util.Random;

/**
 * Os Dez Clãs Elementares - Java ME MIDP 2.0 / CLDC 1.1.
 * Jogo de luta 2D, desenhado por código para reduzir o uso de memória.
 */
public final class DezClansMIDlet extends MIDlet {
    private ArenaCanvas arena;
    public void startApp() {
        if (arena == null) arena = new ArenaCanvas(this);
        Display.getDisplay(this).setCurrent(arena);
        arena.resume();
    }
    public void pauseApp() { if (arena != null) arena.pause(); }
    public void destroyApp(boolean unconditional) { if (arena != null) arena.shutdown(); }
    void quit() { if (arena != null) arena.shutdown(); notifyDestroyed(); }

    private static final class Fighter {
        String name, clan;
        int color, hp, maxHp, x, y, facing, cooldown, flash, anim, invuln;
        boolean guarding, attacking, special;
        Fighter(String n,String c,int col,int health,int px,int py,int face) {
            name=n; clan=c; color=col; hp=maxHp=health; x=px; y=py; facing=face;
        }
    }

    private static final class ArenaCanvas extends Canvas implements Runnable {
        private final DezClansMIDlet host;
        private Thread thread;
        private boolean running, paused, menu=true, ended, won, leftHeld, rightHeld;
        private int sw, sh, stage, score, wins, menuIndex, tick, bannerTicks;
        private long last;
        private String banner="";
        private Fighter player, enemy;
        private final Random rng=new Random();
        private final String[] clans={"CHAMA","MARÉ","TROVÃO","PEDRA","VENTO","GELO","FLORA","SOMBRA","LUZ","ÉTER"};
        private final String[] names={"Ignar","Neris","Voltar","Granok","Zephyra","Glacius","Sylva","Noct","Aurel","Aetherion"};
        private final int[] colors={0xF45A32,0x3E9BE8,0xF4D84A,0xB28A5A,0xB9EAF5,0x7EE6FF,0x54C76B,0x9A65CF,0xFFF0A0,0xE5A7FF};
        private final int[] darks={0x8E2E25,0x174D8D,0x806C20,0x59432F,0x527C8A,0x287C94,0x246E38,0x4C2A72,0x9C8B4C,0x86539B};

        ArenaCanvas(DezClansMIDlet m) {
            host=m; setFullScreenMode(true); sw=getWidth(); sh=getHeight();
            player=new Fighter("ERRANTE","ÉTER",0xE5A7FF,100,sw/4,sh-62,1);
            spawnEnemy();
        }
        void resume() {
            paused=false;
            if(thread==null) { running=true; thread=new Thread(this); thread.start(); }
        }
        void pause() { paused=true; }
        void shutdown() { running=false; }
        protected void sizeChanged(int w,int h) { sw=w; sh=h; if(player!=null) player.y=sh-62; if(enemy!=null) enemy.y=sh-62; }
        private void spawnEnemy() {
            int i=stage%10;
            enemy=new Fighter(names[i],clans[i],colors[i],65+stage*8,sw*3/4,sh-62,-1);
            if(player!=null) player.hp=Math.min(player.maxHp,player.hp+20);
            banner="CLÃ "+clans[i]+" - PREPARE-SE!";
            bannerTicks=65;
        }
        public void run() {
            while(running) {
                long start=System.currentTimeMillis();
                if(!paused) update();
                repaint(); serviceRepaints();
                long delay=40-(System.currentTimeMillis()-start);
                if(delay>0) try { Thread.sleep(delay); } catch(InterruptedException e) {}
            }
            thread=null;
        }
        private void update() {
            tick++;
            if(menu||ended) return;
            Fighter p=player, e=enemy;
            if(p.cooldown>0)p.cooldown--; if(e.cooldown>0)e.cooldown--;
            if(p.flash>0)p.flash--; if(e.flash>0)e.flash--;
            if(p.invuln>0)p.invuln--; if(e.invuln>0)e.invuln--;
            p.anim++; e.anim++;
            if(leftHeld) { p.x=Math.max(18,p.x-3); p.facing=-1; }
            if(rightHeld) { p.x=Math.min(sw-18,p.x+3); p.facing=1; }
            int dx=p.x-e.x;
            if(Math.abs(dx)>48) e.x+=dx>0?2:-2;
            e.facing=p.x>e.x?1:-1;
            if(e.cooldown==0 && Math.abs(p.x-e.x)<76 && rng.nextInt(100)<7) enemyAttack();
            if(bannerTicks>0)bannerTicks--;
            if(p.hp<=0){ended=true;won=false;banner="O torneio ainda não terminou...";}
            if(e.hp<=0){
                score+=100+(stage*35); wins++; stage++;
                if(stage>=10){ended=true;won=true;banner="OS DEZ CLÃS FORAM VENCIDOS!";}
                else spawnEnemy();
            }
        }
        private void enemyAttack() {
            enemy.cooldown=32; enemy.attacking=true; enemy.special=false;
            if(Math.abs(enemy.x-player.x)<70) {
                if(!player.guarding && player.invuln==0) {
                    int d=5+rng.nextInt(7); player.hp-=d; player.flash=6; player.invuln=12;
                    banner="GOLPE DE "+enemy.clan+"!"; bannerTicks=24;
                } else if(player.guarding) { banner="DEFESA!"; bannerTicks=16; }
            }
        }
        private void attack(boolean special) {
            if(player.cooldown>0)return;
            player.cooldown=special?36:19; player.attacking=true; player.special=special;
            int reach=special?105:70;
            if(Math.abs(player.x-enemy.x)<=reach && Math.abs(player.y-enemy.y)<40) {
                int damage=special?20+rng.nextInt(10):9+rng.nextInt(6);
                enemy.hp-=damage; enemy.flash=7; enemy.invuln=4;
                score+=special?10:0;
                banner=(special?"TÉCNICA ELEMENTAL! ":"GOLPE CERTEIRO! ")+damage;
                bannerTicks=24;
            } else { banner="FORA DE ALCANCE!"; bannerTicks=18; }
        }
        protected void keyPressed(int code) {
            int a=getGameAction(code);
            if(menu) {
                if(a==UP||a==DOWN||code==KEY_NUM2||code==KEY_NUM8)menuIndex=1-menuIndex;
                else if(a==FIRE||code==KEY_NUM5) {
                    if(menuIndex==0) { menu=false; ended=false; stage=score=wins=0; player.hp=100; spawnEnemy(); }
                    else host.quit();
                }
                repaint();return;
            }
            if(ended) {
                if(a==FIRE||code==KEY_NUM5||code==KEY_NUM0){menu=true;menuIndex=0;}
                repaint();return;
            }
            if(a==LEFT||code==KEY_NUM4){leftHeld=true;player.facing=-1;}
            if(a==RIGHT||code==KEY_NUM6){rightHeld=true;player.facing=1;}
            if(a==UP||code==KEY_NUM2)player.y=Math.max(sh-112,player.y-8);
            if(a==DOWN||code==KEY_NUM8)player.y=Math.min(sh-62,player.y+8);
            if(a==FIRE||code==KEY_NUM5)attack(false);
            if(code==KEY_NUM7||code==KEY_NUM9)attack(true);
            if(code==KEY_NUM0)player.guarding=true;
        }
        protected void keyReleased(int code) {
            int a=getGameAction(code);
            if(a==LEFT||code==KEY_NUM4)leftHeld=false;
            if(a==RIGHT||code==KEY_NUM6)rightHeld=false;
            if(code==KEY_NUM0)player.guarding=false;
        }
        protected void paint(Graphics g) {
            sw=getWidth();sh=getHeight();
            g.setColor(0x10152A);g.fillRect(0,0,sw,sh);
            if(menu){drawMenu(g);return;}
            drawStage(g); drawFighter(g,player,true);drawFighter(g,enemy,false);drawHud(g);
            if(ended)drawEnd(g);
        }
        private void drawMenu(Graphics g) {
            g.setColor(0x171E3D);g.fillRect(0,0,sw,sh);
            for(int i=0;i<35;i++){g.setColor(colors[i%10]);g.fillRect((i*31+7)%sw,(i*47+13)%sh,2,2);}
            g.setColor(0xF4D84A);g.setFont(Font.getFont(Font.FACE_SYSTEM,Font.STYLE_BOLD,Font.SIZE_LARGE));
            g.drawString("OS DEZ CLÃS",sw/2,sh/5,Graphics.HCENTER|Graphics.TOP);
            g.drawString("ELEMENTARES",sw/2,sh/5+22,Graphics.HCENTER|Graphics.TOP);
            g.setColor(0xDDE5FF);g.setFont(Font.getDefaultFont());
            g.drawString("O torneio dos poderes",sw/2,sh/3,Graphics.HCENTER|Graphics.TOP);
            g.setColor(menuIndex==0?0xF4D84A:0xFFFFFF);g.drawString((menuIndex==0?"> ":"  ")+"INICIAR",sw/2,sh/2+10,Graphics.HCENTER|Graphics.TOP);
            g.setColor(menuIndex==1?0xF4D84A:0xFFFFFF);g.drawString((menuIndex==1?"> ":"  ")+"SAIR",sw/2,sh/2+35,Graphics.HCENTER|Graphics.TOP);
            g.setColor(0xA9B3D8);g.drawString("2/8 navegar  -  5 escolher",sw/2,sh-25,Graphics.HCENTER|Graphics.TOP);
        }
        private void drawStage(Graphics g) {
            int col=colors[stage%10];
            g.setColor(0x202747);g.fillRect(0,0,sw,sh);
            // Arena geométrica com cor do clã.
            g.setColor(0x30395D);
            for(int x=0;x<sw;x+=20)g.drawLine(x,50,x,sh-44);
            for(int y=50;y<sh-44;y+=20)g.drawLine(0,y,sw,y);
            g.setColor(darks[stage%10]);g.fillRect(0,sh-48,sw,4);
            g.setColor(0x111629);g.fillRect(0,sh-44,sw,44);
            g.setColor(col);g.fillRect(sw/2-2,sh-44,4,44);
            // Emblemas do elemento nas laterais.
            g.setColor(col);g.fillRect(12,66,8,8);g.fillRect(sw-20,66,8,8);
        }
        private void drawFighter(Graphics g,Fighter f,boolean isPlayer) {
            int x=f.x,y=f.y;
            int step=(f.anim/5)%2;
            int legY=y+step*2;
            g.setColor(0x090D1B);g.fillRect(x-13,y+17,26,4);
            g.setColor(f.flash>0?0xFFFFFF:darks[stage%10]);g.fillRect(x-10,y-28,20,16);
            g.setColor(f.flash>0?0xFFFFFF:f.color);g.fillRect(x-8,y-35,16,15);
            g.setColor(0x14182A);g.fillRect(x+(f.facing>0?2:-6),y-29,4,3);
            g.setColor(f.flash>0?0xFFFFFF:f.color);g.fillRect(x-12,y-13,24,21);
            // braços em posições alternadas para sugerir animação.
            int armX=x+f.facing*10;
            if(f.attacking) {
                g.setColor(f.special?0xFFF0A0:0xFFFFFF);
                g.fillRect(Math.min(x, x+f.facing*(f.special?42:27)),y-11,
                           Math.max(5,f.special?42:27),6);
                g.setColor(f.color);g.fillRect(armX,y-15,7,7);
            } else {
                g.setColor(f.color);g.fillRect(x-16,y-10,5,13);g.fillRect(x+11,y-10,5,13);
            }
            g.setColor(darks[stage%10]);g.fillRect(x-8, y+8,6,10+step);g.fillRect(x+2,legY+8,6,10);
            g.setColor(f.color);g.fillRect(x-4,y-40,8,4);
            if(f.guarding&&isPlayer){g.setColor(0x83E6FF);g.drawRect(x-19,y-43,38,65);g.drawRect(x-21,y-45,42,69);}
            if(f.invuln>0 && (tick%4<2)) {g.setColor(0xFFFFFF);g.drawRect(x-17,y-42,34,64);}
            f.attacking=false;
        }
        private void drawHud(Graphics g) {
            g.setColor(0x080B17);g.fillRect(0,0,sw,48);
            g.setColor(0xFFFFFF);g.setFont(Font.getDefaultFont());
            g.drawString("ERRANTE",5,2,Graphics.LEFT|Graphics.TOP);
            g.drawString(clans[stage%10],sw-5,2,Graphics.RIGHT|Graphics.TOP);
            g.setColor(0x592535);g.fillRect(5,16,100,8);g.setColor(0x4BE078);g.fillRect(5,16,Math.max(0,100*player.hp/player.maxHp),8);
            g.setColor(0x592535);g.fillRect(sw-105,16,100,8);g.setColor(0xFF625F);g.fillRect(sw-105,16,Math.max(0,100*enemy.hp/enemy.maxHp),8);
            g.setColor(0xDDE2FF);g.drawString("FASE "+(stage+1)+"/10",5,28,Graphics.LEFT|Graphics.TOP);
            g.drawString("PTS "+score,sw-5,28,Graphics.RIGHT|Graphics.TOP);
            if(bannerTicks>0){g.setColor(0xFFFFFF);g.drawString(banner,sw/2,51,Graphics.HCENTER|Graphics.TOP);}
            g.setColor(0x9AA5C8);g.drawString("4/6 mover  5 golpe  7/9 especial",sw/2,sh-24,Graphics.HCENTER|Graphics.TOP);
            g.drawString("0 defender  2/8 mover eixo",sw/2,sh-12,Graphics.HCENTER|Graphics.TOP);
        }
        private void drawEnd(Graphics g) {
            g.setColor(0x080B17);g.fillRect(10,sh/2-48,sw-20,104);
            g.setColor(won?0xF4D84A:0xFF6B70);g.drawRect(10,sh/2-48,sw-20,104);
            g.setColor(0xFFFFFF);g.setFont(Font.getFont(Font.FACE_SYSTEM,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
            g.drawString(won?"CAMPEÃO!":"FIM DE JOGO",sw/2,sh/2-34,Graphics.HCENTER|Graphics.TOP);
            g.setFont(Font.getDefaultFont());g.drawString("Pontuação: "+score,sw/2,sh/2-8,Graphics.HCENTER|Graphics.TOP);
            g.drawString("Clãs vencidos: "+wins,sw/2,sh/2+8,Graphics.HCENTER|Graphics.TOP);
            g.drawString("5 para voltar ao menu",sw/2,sh/2+29,Graphics.HCENTER|Graphics.TOP);
        }
    }
}
