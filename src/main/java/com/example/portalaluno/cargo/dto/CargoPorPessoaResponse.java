package com.example.portalaluno.cargo.dto;

import java.util.UUID;

public record CargoPorPessoaResponse(UUID uuid, String name, java.util.List<String> nomes) {}
