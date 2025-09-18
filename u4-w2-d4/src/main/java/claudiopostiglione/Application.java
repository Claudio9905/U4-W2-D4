package claudiopostiglione;

import claudiopostiglione.entities.Customer;
import claudiopostiglione.entities.Order;
import claudiopostiglione.entities.Product;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        //1
        Supplier<Product> supplierProduct = () -> {
            Faker faker = new Faker();
            Random rdmNumber = new Random();
            Long idProductRandom = rdmNumber.nextLong(0, 10000);
            Double priceRandaom = (double) (Math.round(rdmNumber.nextDouble(0, 200.00) * 100) / 100);

            return new Product(idProductRandom, faker.book().title(), faker.book().genre(), priceRandaom);
        };

        List<Product> listProduct = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            listProduct.add(supplierProduct.get());
        }

        Supplier<Customer> supplierCustomer = () -> {
            Faker faker = new Faker();
            Random rdmNumber = new Random();
            Long idCustomerRandom = rdmNumber.nextLong(0, 10000);
            Integer tierRandom = rdmNumber.nextInt(0, 3);

            return new Customer(idCustomerRandom, faker.lordOfTheRings().character(), tierRandom);
        };

        List<Customer> listCustomer = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            listCustomer.add(supplierCustomer.get());
        }

        Supplier<Order> supplierOrder = () -> {
            Random rdmNumber = new Random();
            Long idRandom = rdmNumber.nextLong(0, 10000);
            Customer customer = supplierCustomer.get();
            LocalDate orderDate = LocalDate.of(2025, 5, 26);
            LocalDate deliveryDate = LocalDate.of(2025, 5, 29);

            return new Order(idRandom, "in consegna", orderDate, deliveryDate, listProduct, customer);
        };

        List<Order> listOrder = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            listOrder.add(supplierOrder.get());
        }

        System.out.println("|------------- Lista degli ordini -------------|");
        System.out.println(listOrder);
        System.out.println("|------------- Lista dei prodotti -------------|");
        System.out.println(listProduct);
        System.out.println("|------------- Lista dei clienti -------------|");
        System.out.println(listCustomer);

        int scelta;

            System.out.println("\n");
        do {
            System.out.println("|-----------------------------|");
            System.out.println("|-----------------------|");
            System.out.println("| Richieste disponibili |");
            System.out.println("| (0) - Exit ");
            System.out.println("| (1) - Raggruppamento degli ordini per cliente");
            System.out.println("| (2) - Totale delle vendite per ogni cliente ( Dato un elenco di ordini ) ");
            System.out.println("| (3) - Prodotti più costosi ( Dato un elenco di prodotti ) ");
            System.out.println("| (4) - Media degli importi degli ordini ( Dato un elenco di ordini )  ");
            System.out.println("| (5) - Prodotti raggruppati per categoria e somma degli importi di ciascuna categoria ( Dato un elenco di prodotti )");
            scelta = Integer.parseInt(scanner.nextLine());

            switch (scelta) {
                case 0:
                    break;
                case 1:
                    ordersByCustomers(listOrder);
                    break;
                case 2:
                    totalSalesByCustomers(listOrder);
                    break;
                case 3:
                    expensiveProducts(listProduct);
                    break;
                case 4:
                    avaregePrices(listOrder);
                    break;
                case 5:
                    productsByCategory(listProduct);
                    break;
                default:
                    System.out.println("Attenzione, scelta inserita errata, prego riprovare..");
            }

        } while (scelta != 0);


    }

    //1.1
    public static void ordersByCustomers(List<Order> listOrder){
        Map<Customer,List<Order>> orderByCustomer = listOrder.stream().collect(Collectors.groupingBy(order -> order.getCustomer()));
        orderByCustomer.forEach(((customer, orders) -> System.out.println("Cliente: " + customer
                + " / Ordini: " + orders)));
    }

    //1.2
    public static void totalSalesByCustomers(List<Order> orders){

        Map<Customer,Double> totalByCustomer = orders.stream().collect(Collectors.groupingBy(order -> order.getCustomer(), Collectors.summingDouble(order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum())));
        totalByCustomer.forEach((customer, value) -> {
                System.out.println("| - Cliente: " + customer);
                System.out.println("| - Totale degli acquisti: " + value);
        });
    };

    //1.3
    public static void expensiveProducts(List<Product> products){
        List<Product> expensiveProduct = products.stream().sorted(Comparator.comparing(Product::getPrice).reversed()).limit(3).toList();
        System.out.println("| - I prodotti più costosi:");
        expensiveProduct.forEach(product -> System.out.println(product));
    }

    //1.4
    public static void avaregePrices(List<Order> orders){
        OptionalDouble avaregePrice = orders.stream().mapToDouble(order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum()).average();

        if (avaregePrice.isPresent()){
            System.out.println("| - La media degli importi degli ordini è: € " + avaregePrice);
        } else {
            System.out.println("| - Errore, impossibile calcolare la media per mancaza di importi");
        }

    }

    //1.5
    public static void productsByCategory(List<Product> products){
        Map<String, Double> totalByCategorys= products.stream().collect(Collectors.groupingBy(product -> product.getCategory(), Collectors.summingDouble(product -> product.getPrice())));
        System.out.println("Somma degli importi per ogni categoria");
        totalByCategorys.forEach((categoria,totale) -> System.out.println("| - Categoria: " + categoria + " / Totale: " + totale));
    }

}
