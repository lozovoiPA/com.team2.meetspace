package com.team2.meetspace.ui.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.meetspace.data.entities.UserContact

@Composable
fun Step3Contacts(
    contacts: List<UserContact>,
    selectedContacts: List<UserContact>,
    onContactToggled: (UserContact, Boolean) -> Unit,
    onSave: () -> Unit
) {
    Column {
        Text(
            "Выберите участников",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Нет контактов для отображения",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(contacts) { contact ->
                    ContactItem(
                        contact = contact,
                        isSelected = selectedContacts.contains(contact),
                        onToggle = { checked -> onContactToggled(contact, checked) }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text("Создать встречу")
        }
    }
}

@Composable
private fun ContactItem(
    contact: UserContact,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = isSelected,
                onValueChange = onToggle,
                role = Role.Checkbox
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = null
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = contact.displayName,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (contact.phone != null) {
                Text(
                    text = contact.phone,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}