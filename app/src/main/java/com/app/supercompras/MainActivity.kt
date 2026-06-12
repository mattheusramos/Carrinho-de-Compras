package com.app.supercompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.Typography
import androidx.compose.runtime.collectAsState
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
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    val viewModel: ComprasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperComprasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListaDeCompras(Modifier.padding(innerPadding), viewModel)
                }
            }
        }
    }
}

@Composable
fun ListaDeCompras(modifier: Modifier = Modifier, viewModel: ComprasViewModel) {
    val listaDeItens by viewModel.listaDeItens.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        item {
            LogoTopo()
            AdicionarItem(onSalvarItem = { novoItem ->
                viewModel.adicionarItem(novoItem)
            })
            Spacer(modifier = Modifier.height(48.dp))
            Titulo(
                texto = "Lista de Compras",
            )
        }
        ListaDeItems(
            lista = listaDeItens.filter { !it.foiComprado },
            aoMudarStatus = { itemSelecionado ->
                viewModel.mudarStatus(itemSelecionado)
            },
            aoRemoverItem = { itemRemovido ->
                viewModel.removerItem(itemRemovido)
            },
            aoEditarItem = { itemEditado, novoTexto ->
                viewModel.editarItem(itemEditado, novoTexto)
            }
        )

        item {
            Titulo(texto = "Comprados")
        }

        if (listaDeItens.any { it.foiComprado }) {
            ListaDeItems(
                lista = listaDeItens.filter { it.foiComprado },
                aoMudarStatus = { itemSelecionado ->
                    viewModel.mudarStatus(itemSelecionado)
                },
                aoRemoverItem = { itemRemovido ->
                    viewModel.removerItem(itemRemovido)
                },
                aoEditarItem = { itemEditado, novoTexto ->
                    viewModel.editarItem(itemEditado, novoTexto)
                }
            )
        }
    }
}

fun LazyListScope.ListaDeItems(
    lista: List<ItemCompra>,
    aoMudarStatus: (item: ItemCompra) -> Unit,
    aoEditarItem: (item: ItemCompra, novoTexto: String) -> Unit = { _, _ -> },
    aoRemoverItem: (item: ItemCompra) -> Unit,
) {
    items(lista.size) { index ->
        ItemDaLista(
            item = lista[index],
            aoMudarStatus = aoMudarStatus,
            aoRemoverItem = aoRemoverItem,
            aoEditarItem = aoEditarItem
        )
    }
}

@Composable
fun AdicionarItem(onSalvarItem: (item: ItemCompra) -> Unit, modifier: Modifier = Modifier) {
    var texto by rememberSaveable { mutableStateOf("") }
    OutlinedTextField(
        value = texto,
        onValueChange = { texto = it },
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
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Coral,
        ),
        onClick = {
            onSalvarItem(ItemCompra(texto, false, getDataHora()))
            texto = ""
        },
        modifier = modifier
    ) {
        Text(
            text = "Salvar item",
            color = Color.White,
            style = Typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

fun getDataHora(): String {
    val dataHoraAtual = System.currentTimeMillis()
    val dataHoraFormatada = SimpleDateFormat("EEEE (dd/MM/yyyy) 'às' HH:mm", Locale("pt", "BR"))
    return dataHoraFormatada.format(dataHoraAtual)
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
    aoEditarItem: (item: ItemCompra, novoTexto: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.Top, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            var textoEditado by rememberSaveable() { mutableStateOf(item.texto) }
            var edicao by rememberSaveable() { mutableStateOf(false) }

            Checkbox(
                checked = item.foiComprado,
                onCheckedChange = {
                    aoMudarStatus(item)
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .requiredSize(24.dp)
            )

            if (edicao) {
                OutlinedTextField(
                    value = textoEditado,
                    onValueChange = { textoEditado = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )

                IconButton(
                    onClick = {
                        aoEditarItem(item, textoEditado)
                        edicao = false
                    }
                ) {
                    Icone(
                        Icons.Default.Done,
                        modifier = Modifier
                            .size(16.dp)
                    )
                }
            } else {
                Text(
                    text = item.texto,
                    modifier = Modifier.weight(1f),
                    style = Typography.bodyMedium,
                    textAlign = TextAlign.Start
                )
            }

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

            IconButton(
                onClick = {
                    edicao = true
                }
            ) {
                Icone(
                    Icons.Default.Edit,
                    modifier = Modifier
                        .size(16.dp)
                )
            }
        }
        Text(
            item.datahora,
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
        ItemDaLista(item = ItemCompra("Suco", false, "Segunda-feira"))
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
    var foiComprado: Boolean = false,
    val datahora: String
)