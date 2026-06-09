package com.app.supercompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.supercompras.ui.theme.SuperComprasTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.Typography
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.app.supercompras.ui.theme.Coral
import com.app.supercompras.ui.theme.Marinho
import com.app.supercompras.ui.theme.Typography
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperComprasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListaDeCompras(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ListaDeCompras(modifier: Modifier = Modifier) {
    var listaDeItens by rememberSaveable() { mutableStateOf(listOf<ItemCompra>()) }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        LogoTopo()
        AdicionarItem(onSalvarItem = { textoNovo ->
            listaDeItens = listaDeItens + ItemCompra(textoNovo)
        })
        Spacer(modifier = Modifier.height(48.dp))
        Titulo(
            texto = "Lista de Compras",
        )
        ListaDeItems(
            lista = listaDeItens.filter { !it.foiComprado },
            aoMudarStatus = { itemSelecionado ->
                listaDeItens = listaDeItens.map { itemMap ->
                    if (itemSelecionado == itemMap) {
                        itemSelecionado.copy(foiComprado = !itemSelecionado.foiComprado)
                    } else {
                        itemMap
                    }
                }
            },
            aoRemoverItem = { itemRemovido ->
                listaDeItens = listaDeItens - itemRemovido
            },
            aoEditarItem = { itemEditado ->
                listaDeItens = listaDeItens.map { itemAtual ->
                    if (itemAtual == itemEditado) {
                        itemAtual.copy(texto = itemEditado.texto)
                    } else {
                        itemAtual
                    }
                }
            }
        )
        Titulo(texto = "Comprados")

        if (listaDeItens.any { it.foiComprado }) {
            ListaDeItems(
                lista = listaDeItens.filter { it.foiComprado },
                aoMudarStatus = { itemSelecionado ->
                    listaDeItens = listaDeItens.map { itemMap ->
                        if (itemSelecionado == itemMap) {
                            itemSelecionado.copy(foiComprado = !itemSelecionado.foiComprado)
                        } else {
                            itemMap
                        }
                    }
                },
                aoRemoverItem = { itemRemovido ->
                    listaDeItens = listaDeItens - itemRemovido
                },
                aoEditarItem = { itemEditado ->
                    listaDeItens = listaDeItens.map { itemAtual ->
                        if (itemAtual == itemEditado) {
                            itemAtual.copy(texto = itemEditado.texto)
                        } else {
                            itemAtual
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ListaDeItems(
    lista: List<ItemCompra>,
    aoMudarStatus: (item: ItemCompra) -> Unit,
    aoEditarItem: (item: ItemCompra) -> Unit,
    aoRemoverItem: (item: ItemCompra) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        lista.forEach { item ->
            ItemDaLista(
                item,
                aoMudarStatus = aoMudarStatus,
                aoRemoverItem = aoRemoverItem,
                aoEditarItem = aoEditarItem
            )
        }
    }
}

@Composable
fun AdicionarItem(onSalvarItem: (texto: String) -> Unit, modifier: Modifier = Modifier) {
    var texto = rememberSaveable { mutableStateOf("") }
    OutlinedTextField(
        value = texto.value,
        onValueChange = { texto.value = it },
        placeholder = {
            Text(
                text = "Digite o item que deseja adicionar",
                color = Color.Gray,
                style = Typography.bodyMedium
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )

    Button(
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Coral,
        ),
        onClick = {
            onSalvarItem(texto.value)
            texto.value = ""
        },
        modifier = modifier
    ) {
        Text(
            text = "Salvar item",
            color = Color.White,
            style = Typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun Titulo(texto: String, modifier: Modifier = Modifier) {
    Text(text = texto, modifier = modifier, style = Typography.headlineLarge)
}

@Composable
fun ItemDaLista(
    item: ItemCompra,
    aoMudarStatus: (item: ItemCompra) -> Unit = {},
    aoRemoverItem: (item: ItemCompra) -> Unit = {},
    aoEditarItem: (item: ItemCompra) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.Top, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            var textoEditado by rememberSaveable() { mutableStateOf(item.texto) }

            Checkbox(
                checked = item.foiComprado,
                onCheckedChange = {
                    aoMudarStatus(item)
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .requiredSize(24.dp)
            )
            Text(
                text = item.texto,
                modifier = Modifier.weight(1f),
                style = Typography.bodyMedium,
                textAlign = TextAlign.Start
            )
            IconButton(
                onClick = { aoRemoverItem(item) },
                modifier = Modifier
                    .padding(end = 8.dp)
            ) {
                Icone(
                    Icons.Default.Delete,
                    modifier = Modifier
                        .size(16.dp)
                )
            }
            OutlinedTextField(
                value = textoEditado,
                onValueChange = { textoEditado.value = it },

            )
            IconButton(
                onClick = { aoEditarItem(item) }
            ) {
                Icone(
                    Icons.Default.Edit,
                    modifier = Modifier
                        .size(16.dp)
                )
            }
        }
        Text(
            text = "Segunda-feira (31/10/2022) às 08:30",
            Modifier.padding(top = 8.dp),
            style = Typography.labelSmall
        )
    }
}

@Composable
fun LogoTopo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logoapp),
        contentDescription = null,
        modifier = modifier.size(160.dp)
    )
}

@Composable
fun Icone(icone: ImageVector, modifier: Modifier = Modifier) {
    Icon(icone, contentDescription = "Editar", modifier = modifier, tint = Marinho)
}

@Preview
@Composable
private fun AdicionarItemPreview() {
    SuperComprasTheme {
        AdicionarItem(onSalvarItem = {})
    }
}

@Preview
@Composable
private fun IconePreview() {
    SuperComprasTheme() {
        Icone(icone = Icons.Default.Delete)
    }
}

@Preview
@Composable
private fun ItemDaListaPreview() {
    SuperComprasTheme {
        ItemDaLista(item = ItemCompra("Suco"))
    }
}

@Preview
@Composable
private fun LogoTopoPreview() {
    SuperComprasTheme() {
        LogoTopo()
    }
}

@Preview
@Composable
private fun TituloPreview() {
    SuperComprasTheme() {
        Titulo(texto = "Lista de Compras")
    }
}

data class ItemCompra(
    val texto: String,

    var foiComprado: Boolean = false
)