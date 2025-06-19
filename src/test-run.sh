#!/bin/bash

set -e

echo "🔄 Subindo container do PostgreSQL de teste..."
docker-compose -f docker-compose.test.yml up -d

echo "⏳ Aguardando o PostgreSQL ficar pronto..."
sleep 10  

echo "✅ Rodando testes com perfil 'test'..."
./mvnw test -Dspring.profiles.active=test

echo "🧹 Derrubando container de teste..."
docker-compose -f docker-compose.test.yml down

echo "✅ Testes finalizados com sucesso."