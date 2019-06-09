package Human;

public class Human
{
    int age;
    String name;
    int mass;

    public void SetPerson(int age1, String name1, int mass1)
    {
        age = age1;
        name = name1;
        mass = mass1;
        System.out.println("Человек по имени " + name + " с весом " + mass + " и в возрасте " + age + " успешно добавлен" );
    }
}