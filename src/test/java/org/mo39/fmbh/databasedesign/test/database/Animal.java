package org.mo39.fmbh.databasedesign.test.database;

public class Animal {

  private int animal_ID;
  private String name;
  private byte sector;

  public int getAnimal_ID() {
    return animal_ID;
  }

  public void setAnimal_ID(int animal_ID) {
    this.animal_ID = animal_ID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte getSector() {
    return sector;
  }

  public void setSector(byte sector) {
    this.sector = sector;
  }

  public static void main(String[] args) {
    System.out.println(Animal.class.getName());
  }
}
