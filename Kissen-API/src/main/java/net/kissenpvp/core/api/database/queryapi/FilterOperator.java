package net.kissenpvp.core.api.database.queryapi;

/**
 * The {@code FilterOperator} enumeration is used to define the logical operators
 * that are used to link together multiple filter conditions in a query. Each operator
 * type represents a different way of logically connecting filters.
 *
 * <p> Each {@code FilterOperator} value corresponds to a logical operation:
 *
 * <ul>
 * <li>{@code OR} indicates that the record can match any of the linked conditions.
 * <li>{@code AND} insists that the record must match all of the connected conditions.
 * <li>{@code INIT} serves as an initial operator to start a query or when an operator is not required.
 * </ul>
 *
 * <p> Note: The operator's behavior is dependent on how it is interpreted during query construction.
 */
public enum FilterOperator {
    OR, AND, INIT
}
