import java.util.Random;
import java.util.Scanner;

public class Main {

    public interface Weapon {
        double getDamage();
        void upgradeWeapon();
    }

    public static class RPGCharacter {
        private String name;
        private int level;
        private double swordBaseDamage = 50;
        private double staffBaseDamage = 40;
        private double gunBaseDamage = 60;
        private double shieldBaseDefense = 30;
        private double baseRunSpeed = 10;
        private double currentHP;
        private Weapon weapon;
        private boolean isDefending = false; // เพิ่มตัวแปรเพื่อเก็บสถานะการป้องกัน

        public RPGCharacter(String name, int level, Weapon weapon) {
            this.name = name;
            this.level = level;
            this.weapon = weapon;
            this.currentHP = getMaxHP();
        }

        public void levelUp() {
            this.level++;
            this.currentHP = getMaxHP(); //HP เต็มเมื่อเลเวลอัพ
        }

        public double getMaxHP() {
            return 100 + 10 * level; // เลือด + เลเวล * 10
        }

        public double getWeaponDamage() {
            return weapon.getDamage();
        }

        public double getShieldDefense() {
            return shieldBaseDefense * (1 + 0.05 * level); // โล่เพิ่ม 5% ต่อเลเวล
        }

        public double calculateDamageWithDefense(double damage) {
            if (isDefending) {
                // ลดความเสียหายครึ่งหนึ่งเมื่อป้องกัน
                return damage / 2;
            }
            return damage;
        }

        public void takeDamage(double damage) {
            currentHP -= damage;
            if (currentHP < 0) {
                currentHP = 0;
            }
        }

        public boolean isAlive() {
            return currentHP > 0;
        }

        public void startDefense() {
            isDefending = true;
        }

        public void stopDefense() {
            isDefending = false;
        }

        public String getStatus() {
            return String.format("%-20s\n%-20s\n%-20s", name, "Level: " + level, "HP: " + currentHP + " / " + getMaxHP());
        }

        public void increaseWeaponDamage() {
            weapon.upgradeWeapon();
        }

        public void increaseHP() {
            currentHP += 50;
            if (currentHP > getMaxHP()) {
                currentHP = getMaxHP();
            }
        }
    }

    public static class Enemy {
        private String name;
        private int level;
        private double currentHP;
        private double baseDamage;

        public Enemy(String name, int level) {
            this.name = name;
            this.level = level;
            this.baseDamage = 10 + 5 * level;
            this.currentHP = getMaxHP();
        }

        public double getMaxHP() {
            return 80 + 15 * level;
        }

        public int getAttackDamage() {
            Random random = new Random();
            return (int) (baseDamage * (1 + random.nextDouble() * 0.2)); // สุ่มความเสียหาย +-20%
        }

        public boolean evadeAttack() {
            Random random = new Random();
            double evadeChance = 0.1 + (0.02 * level); // โอกาสหลบ = 10% + 2% ต่อเลเวล
            return random.nextDouble() < evadeChance;
        }

        public void takeDamage(double damage) {
            currentHP -= damage;
            if (currentHP < 0) {
                currentHP = 0;
            }
        }

        public boolean isAlive() {
            return currentHP > 0;
        }

        public String getStatus() {
            return String.format("%-20s\n%-20s\n%-20s", name, "Level: " + level, "HP: " + currentHP + " / " + getMaxHP());
        }
    }

    public static class Sword implements Weapon {
        private double damage;
        public Sword(int level) {
            this.damage = 50 * (1 + 0.1 * level); // ดาบเพิ่ม 10% ต่อเลเวล
        }
        @Override
        public double getDamage() {
            return damage;
        }
        @Override
        public void upgradeWeapon() {
            damage *= 1.2; // เพิ่มความแรงดาบ
        }
    }

    public static class Staff implements Weapon {
        private double damage;
        public Staff(int level) {
            this.damage = 40 * (1 + 0.08 * level); // คทาเวทย์เพิ่ม 8% ต่อเลเวล
        }
        @Override
        public double getDamage() {
            return damage;
        }
        @Override
        public void upgradeWeapon() {
            damage *= 1.2; // เพิ่มความแรงของคทาเวทย์
        }
    }

    public static class Gun implements Weapon {
        private double damage;
        public Gun(int level) {
            this.damage = 60 * (1 + 0.12 * level); // ปืนเพิ่ม 12% ต่อเลเวล
        }
        @Override
        public double getDamage() {
            return damage;
        }
        @Override
        public void upgradeWeapon() {
            damage *= 1.2; // เพิ่มความแรงของปืน
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // สร้างตัวละครผู้เล่น
        System.out.print("Please name your character : ");
        String name = scanner.nextLine();

        System.out.print("Please set your character's level ( 1-10 ) : ");
        int level = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\nPlease choose your weapon : ");
        System.out.println("1. Sword");
        System.out.println("2. Staff");
        System.out.println("3. Gun");
        System.out.print("Please select a weapon ( 1 - 3 ) : ");
        int weaponChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Weapon weapon = null;
        switch (weaponChoice) {
            case 1:
                weapon = new Sword(level);
                break;
            case 2:
                weapon = new Staff(level);
                break;
            case 3:
                weapon = new Gun(level);
                break;
            default:
                System.out.println("Invalid choice, selecting random weapon.");
                weapon = getRandomWeapon(level);
        }

        RPGCharacter character = new RPGCharacter(name, level, weapon);

        boolean continueFighting = true;

        while (continueFighting) {
            // สร้างศัตรู
            Enemy enemy = new Enemy("Zendoza", character.level + 1);

            System.out.println("\n================ The battle begins ================");
            while (character.isAlive() && enemy.isAlive()) {
                // แสดงสถานะในรูปแบบหลายบรรทัด (ตัวละครเราอยู่ซ้าย ศัตรูอยู่ขวา)
                System.out.println("\n==================== Current round status ====================");
                System.out.printf("%-30s | %30s\n", character.getStatus().split("\n")[0], enemy.getStatus().split("\n")[0]); // ชื่อ
                System.out.printf("%-30s | %30s\n", character.getStatus().split("\n")[1], enemy.getStatus().split("\n")[1]); // เลเวล
                System.out.printf("%-30s | %30s\n", character.getStatus().split("\n")[2], enemy.getStatus().split("\n")[2]); // HP
                System.out.println("------------------------------------------------------------");

                // เมนูการต่อสู้
                System.out.println("\n==== choice ====");
                System.out.println("1. Attack");
                System.out.println("2. Defense");
                System.out.println("3. Escape");
                System.out.print("Please select your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1: // โจมตี
                        double damage = character.getWeaponDamage();
                        if (enemy.evadeAttack()) {
                            System.out.println("Enemies can dodge attacks!");
                        } else {
                            enemy.takeDamage(damage);
                            System.out.println("You attack enemies with damage: " + damage);
                        }

                        // หลังจากโจมตีผู้เล่นแล้ว ให้ศัตรูโจมตีกลับ
                        if (enemy.isAlive()) {
                            // คำนวณดาเมจจากศัตรู
                            int enemyDamage = enemy.getAttackDamage();
                            double damageWithDefense = character.calculateDamageWithDefense(enemyDamage); // คำนวณดาเมจหลังจากการป้องกัน
                            character.takeDamage(damageWithDefense);
                            System.out.println("Enemies attack you with damage: " + damageWithDefense);
                        }
                        break;
                    case 2: // ป้องกัน
                        character.startDefense();
                        System.out.println("You defend and reduce incoming damage by half.");
                        break;
                    case 3: // หนี
                        System.out.println("You try to escape, but fail!");
                        break;
                    default:
                        System.out.println("Invalid choice! Try again.");
                }

                // ปิดสถานะการป้องกัน
                character.stopDefense();
            }

            if (!character.isAlive()) {
                System.out.println("\nYou have been defeated by the enemy.");
            } else {
                System.out.println("\nYou defeated the enemy!");
            }

            // ถามว่าต้องการต่อสู้ใหม่หรือไม่
            System.out.print("Do you want to fight another enemy? (Y/N): ");
            char continueChoice = scanner.next().charAt(0);
            continueFighting = (continueChoice == 'Y' || continueChoice == 'y');
        }

        scanner.close();
    }

    private static Weapon getRandomWeapon(int level) {
        Random random = new Random();
        int choice = random.nextInt(3) + 1; // Random choice between 1 and 3
        switch (choice) {
            case 1:
                return new Sword(level);
            case 2:
                return new Staff(level);
            case 3:
                return new Gun(level);
            default:
                return new Sword(level); // Default case
        }
    }
}
