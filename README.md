# tf2puml

Generate PlantUML diagram from Terraform files.

## Development

Generate the ANTLR4 parser classes:

```bash
./mvnw antlr4:antlr4
```

View the parse tree:

```bash
mkdir /tmp/tf2puml
cp src/main/antlr4/com/t_horie/tf2puml/application/service/parser/Terraform.g4 /tmp/tf2puml
cd /tmp/tf2puml
antlr4 Terraform.g4
javac Terraform*.java
grun Terraform file_ -gui <path-to-terraform-file>
```

## Build

```bash
./mvnw clean package
```

## Run

```bash
./target/tf2puml-0.0.1-SNAPSHOT.jar --from=<path-to-terraform-file> --to=<path-to-plantuml-file>
```