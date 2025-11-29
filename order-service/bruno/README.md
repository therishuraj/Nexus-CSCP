# Bruno Collection for Order Service

This collection exercises all Order Service REST endpoints (imperative Spring MVC).

## Endpoints Covered
1. POST /api/v1/orders           -> Place new order
2. GET  /api/v1/orders/{id}      -> Get order by ID
3. PUT  /api/v1/orders/{id}/status -> Update order status
4. PUT  /api/v1/orders/{id}/pay  -> Mark supplier as paid
5. GET  /api/v1/orders?userId=... -> Get orders by user (funder or supplier)
6. GET  /api/v1/orders/views?userId=... -> Get order views (read model)

## Usage Steps
1. Open this folder in Bruno (Open Collection).
2. Select environment Local.
3. Run 01-place-order; copy returned `id`.
4. Paste the id into requests 02, 03, 04 where placeholder ORDER_ID is used.
5. Adjust `user_id` in environment file if needed.

## Notes
- Adjust base_url if service runs on a different port.
- Status values example: PLACED, PROCESSING, SHIPPED, DELIVERED.
- Ensure MongoDB has required collections (`orders`, `order_views`).

