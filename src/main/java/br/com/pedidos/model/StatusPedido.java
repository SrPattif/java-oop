package br.com.pedidos.model;

import java.util.Set;

public enum StatusPedido {

    PENDENTE,
    PAGO,
    ENVIADO,
    ENTREGUE,
    CANCELADO;

    public Set<StatusPedido> proximosPermitidos() {
        return switch (this) {
            case PENDENTE -> Set.of(PAGO, CANCELADO);
            case PAGO -> Set.of(ENVIADO, CANCELADO);
            case ENVIADO -> Set.of(ENTREGUE);
            case ENTREGUE, CANCELADO -> Set.of();
        };
    }

    public boolean podeMudarPara(StatusPedido novo) {
        return proximosPermitidos().contains(novo);
    }
}
