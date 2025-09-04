## Migración a Microservicios

PROBAR


```bash
# Example 1: Make a sale with normal price
curl --location 'http://localhost:8081/api/sales?quantity=1' \
--header 'Content-Type: application/json' \
--data '{
  "id": 1,
  "name": "Laptop Dell Inspiron 15",
  "price": 899.99,
  "stockQuantity": 7
}'

# Example 2: Make a sale with discounted price
curl --location 'http://localhost:8081/api/sales?quantity=1' \
--header 'Content-Type: application/json' \
--data '{
  "id": 1,
  "name": "Laptop Dell Inspiron 15",
  "price": 0.99,
  "stockQuantity": 7
}'